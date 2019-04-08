package dbryla.game.yetanotherengine.domain.operations;

import static dbryla.game.yetanotherengine.domain.spells.SpellConstants.UNLIMITED_TARGETS;
import static dbryla.game.yetanotherengine.domain.spells.SpellType.DAMAGE;
import static dbryla.game.yetanotherengine.domain.spells.SpellType.EFFECT;
import static dbryla.game.yetanotherengine.domain.spells.SpellType.HEAL;

import dbryla.game.yetanotherengine.domain.Instrument;
import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.events.EventsFactory;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.spells.SpellSaveType;
import dbryla.game.yetanotherengine.domain.subjects.classes.Subject;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component("spellCastOperation")
public class SpellCastOperation implements Operation {

  private final EventHub eventHub;
  private final FightHelper fightHelper;
  private final EffectConsumer effectConsumer;
  private final EventsFactory eventsFactory;

  @Override
  public Set<Subject> invoke(Subject source, Instrument instrument, Subject... targets) throws UnsupportedGameOperationException {
    Spell spell = instrument.getSpell();
    verifyTargetsNumber(targets, spell);
    Set<Subject> changes = new HashSet<>();
    if (DAMAGE.equals(spell.getSpellType())) {
      changes = tryToDealDamage(source, spell, targets);
    }
    if (EFFECT.equals(spell.getSpellType())) {
      for (Subject target : targets) {
        changes.add(applyEffect(source, spell, target));
      }
    }
    if (HEAL.equals(spell.getSpellType())) {
      for (Subject target : targets) {
        changes.add(heal(source, spell, target));
      }
    }
    effectConsumer.apply(source).ifPresent(changes::add);
    return changes;
  }

  private void verifyTargetsNumber(Subject[] targets, Spell spell) throws UnsupportedSpellCastException {
    if (!unlimitedTargets(spell) && spell.getMaximumNumberOfTargets() < targets.length) {
      throw new UnsupportedSpellCastException("Can't invoke spell " + spell + " on " + targets.length + " targets.");
    }
  }

  private boolean unlimitedTargets(Spell spell) {
    return spell.getMaximumNumberOfTargets() == UNLIMITED_TARGETS;
  }

  private Set<Subject> tryToDealDamage(Subject source, Spell spell, Subject[] targets) {
    Set<Subject> changes = new HashSet<>();
    if (SpellSaveType.ARMOR_CLASS.equals(spell.getSpellSaveType())) {
      handleSpellAttack(changes, source, spell, targets);
    }
    if (SpellSaveType.CONSTITUTION_SAVING_THROW.equals(spell.getSpellSaveType())) {
      handleSavingThrow(changes, source, spell, targets,
          fightHelper::getConstitutionSavingThrow,
          target -> eventHub.send(eventsFactory.failEventBySavingThrow(source, spell, target, "Constitution")));
    }
    if (SpellSaveType.DEXTERITY_SAVING_THROW.equals(spell.getSpellSaveType())) {
      handleSavingThrow(changes, source, spell, targets,
          fightHelper::getDexteritySavingThrow,
          target -> eventHub.send(eventsFactory.failEventBySavingThrow(source, spell, target, "Dexterity")));
    }
    if (SpellSaveType.DEXTERITY_HALF_SAVING_THROW.equals(spell.getSpellSaveType())) {
      handleSavingThrow(changes, source, spell, targets, fightHelper::getDexteritySavingThrow, target -> {
        int attackDamage = spell.spellRoll() / 2;
        attackDamage += getModifier(source, spell);
        changes.add(dealDamage(source, target, attackDamage, spell));
      });
    }
    return changes;
  }

  private int getModifier(Subject source, Spell spell) {
    if (spell.isModifierApply()) {
      return fightHelper.getModifier(source);
    }
    return 0;
  }

  private void handleSpellAttack(Set<Subject> changes, Subject source, Spell spell, Subject[] targets) {
    for (Subject target : targets) {
      HitRoll hitRoll = fightHelper.getHitRoll(source, target);
      hitRoll.addModifier(fightHelper.getModifier(source));
      HitResult hitResult = HitResult.of(hitRoll, target);
      if (!hitResult.isTargetHit()) {
        eventHub.send(eventsFactory.failEvent(source, target, spell.toString(), hitResult));
      } else {
        int attackDamage = fightHelper.getAttackDamage(spell.spellRoll(), hitResult);
        attackDamage += getModifier(source, spell);
        changes.add(dealDamage(source, target, attackDamage, spell, hitResult));
      }
    }
  }

  private Subject dealDamage(Subject source, Subject target, int attackDamage, Spell spell, HitResult hitResult) {
    int remainingHealthPoints = target.getCurrentHealthPoints() - attackDamage;
    Subject changedTarget = target.of(remainingHealthPoints);
    eventHub.send(eventsFactory.successSpellCastEvent(source, changedTarget, spell, hitResult));
    return changedTarget;
  }

  private void handleSavingThrow(Set<Subject> changes, Subject source, Spell spell,
      Subject[] targets, Function<Subject, Integer> savingThrowSupplier,
      Consumer<Subject> failAction) {
    for (Subject target : targets) {
      int savingThrow = savingThrowSupplier.apply(target);
      if (fightHelper.isSaved(source, savingThrow)) {
        failAction.accept(target);
      } else {
        int attackDamage = spell.spellRoll();
        attackDamage += getModifier(source, spell);
        changes.add(dealDamage(source, target, attackDamage, spell));
      }
    }
  }

  private Subject dealDamage(Subject source, Subject target, int attackDamage, Spell spell) {
    int remainingHealthPoints = target.getCurrentHealthPoints() - attackDamage;
    Subject changedTarget = target.of(remainingHealthPoints);
    eventHub.send(eventsFactory.successSpellCastEvent(source, changedTarget, spell));
    return changedTarget;
  }

  private Subject applyEffect(Subject source, Spell spell, Subject target) {
    Subject changedTarget = target.of(spell.getSpellEffect());
    eventHub.send(eventsFactory.successSpellCastEvent(source, changedTarget, spell));
    return changedTarget;
  }

  private Subject heal(Subject source, Spell spell, Subject target) {
    int healRoll = spell.spellRoll();
    healRoll += getModifier(source, spell);
    Subject changedTarget = target.of(target.getCurrentHealthPoints() + healRoll);
    eventHub.send(eventsFactory.successHealEvent(source, changedTarget));
    return changedTarget;
  }

  @Override
  public int getAllowedNumberOfTargets(Instrument instrument) {
    return instrument.getSpell().getMaximumNumberOfTargets();
  }
}
