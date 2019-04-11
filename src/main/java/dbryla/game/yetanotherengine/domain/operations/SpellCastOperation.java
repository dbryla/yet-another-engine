package dbryla.game.yetanotherengine.domain.operations;

import static dbryla.game.yetanotherengine.domain.spells.SpellConstants.UNLIMITED_TARGETS;
import static dbryla.game.yetanotherengine.domain.spells.SpellType.DAMAGE;
import static dbryla.game.yetanotherengine.domain.spells.SpellType.EFFECT;
import static dbryla.game.yetanotherengine.domain.spells.SpellType.HEAL;

import dbryla.game.yetanotherengine.domain.Instrument;
import dbryla.game.yetanotherengine.domain.events.Event;
import dbryla.game.yetanotherengine.domain.events.EventsFactory;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.spells.SpellSaveType;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component("spellCastOperation")
public class SpellCastOperation implements Operation {

  private final FightHelper fightHelper;
  private final EffectConsumer effectConsumer;
  private final EventsFactory eventsFactory;

  @Override
  public OperationResult invoke(Subject source, Instrument instrument, Subject... targets) throws UnsupportedGameOperationException {
    Spell spell = instrument.getSpell();
    verifyTargetsNumber(targets, spell);
    OperationResult operationResult = new OperationResult();
    if (DAMAGE.equals(spell.getSpellType())) {
      OperationResult op = tryToDealDamage(source, spell, targets);
      operationResult.addAll(op.getChangedSubjects(), op.getEmittedEvents());
    }
    if (EFFECT.equals(spell.getSpellType())) {
      for (Subject target : targets) {
        OperationResult op = applyEffect(source, spell, target);
        operationResult.addAll(op.getChangedSubjects(), op.getEmittedEvents());
      }
    }
    if (HEAL.equals(spell.getSpellType())) {
      for (Subject target : targets) {
        OperationResult op = heal(source, spell, target);
        operationResult.addAll(op.getChangedSubjects(), op.getEmittedEvents());
      }
    }
    effectConsumer.apply(source).ifPresent(op -> operationResult.addAll(op.getChangedSubjects(), op.getEmittedEvents()));
    return operationResult;
  }

  private void verifyTargetsNumber(Subject[] targets, Spell spell) throws UnsupportedSpellCastException {
    if (!unlimitedTargets(spell) && spell.getMaximumNumberOfTargets() < targets.length) {
      throw new UnsupportedSpellCastException("Can't invoke spell " + spell + " on " + targets.length + " targets.");
    }
  }

  private boolean unlimitedTargets(Spell spell) {
    return spell.getMaximumNumberOfTargets() == UNLIMITED_TARGETS;
  }

  private OperationResult tryToDealDamage(Subject source, Spell spell, Subject[] targets) {
    if (SpellSaveType.ARMOR_CLASS.equals(spell.getSpellSaveType())) {
      return handleSpellAttack(source, spell, targets);
    }
    if (SpellSaveType.CONSTITUTION_SAVING_THROW.equals(spell.getSpellSaveType())) {
      return handleSavingThrow(source, spell, targets,
          fightHelper::getConstitutionSavingThrow,
          target -> new OperationResult().add(eventsFactory.failEventBySavingThrow(source, spell, target, "Constitution")));
    }
    if (SpellSaveType.DEXTERITY_SAVING_THROW.equals(spell.getSpellSaveType())) {
      return handleSavingThrow(source, spell, targets,
          fightHelper::getDexteritySavingThrow,
          target -> new OperationResult().add(eventsFactory.failEventBySavingThrow(source, spell, target, "Dexterity")));
    }
    if (SpellSaveType.DEXTERITY_HALF_SAVING_THROW.equals(spell.getSpellSaveType())) {
      return handleSavingThrow(source, spell, targets, fightHelper::getDexteritySavingThrow, target -> {
        int attackDamage = spell.spellRoll() / 2;
        attackDamage += getModifier(source, spell);
        return dealDamage(source, target, attackDamage, spell);
      });
    }
    return new OperationResult();
  }

  private int getModifier(Subject source, Spell spell) {
    if (spell.isModifierApply()) {
      return fightHelper.getModifier(source);
    }
    return 0;
  }

  private OperationResult handleSpellAttack(Subject source, Spell spell, Subject[] targets) {
    OperationResult operationResult = new OperationResult();
    for (Subject target : targets) {
      HitRoll hitRoll = fightHelper.getHitRoll(source, target);
      hitRoll.addModifier(fightHelper.getModifier(source));
      HitResult hitResult = HitResult.of(hitRoll, target);
      if (!hitResult.isTargetHit()) {
        operationResult.add(eventsFactory.failEvent(source, target, spell.toString(), hitResult));
      } else {
        int attackDamage = fightHelper.getAttackDamage(spell.spellRoll(), hitResult);
        attackDamage += getModifier(source, spell);
        OperationResult op = dealDamage(source, target, attackDamage, spell, hitResult);
        operationResult.addAll(op.getChangedSubjects(), op.getEmittedEvents());
      }
    }
    return operationResult;
  }

  private OperationResult dealDamage(Subject source, Subject target, int attackDamage, Spell spell, HitResult hitResult) {
    int remainingHealthPoints = target.getCurrentHealthPoints() - attackDamage;
    Subject changedTarget = target.of(remainingHealthPoints);
    Event event = eventsFactory.successSpellCastEvent(source, changedTarget, spell, hitResult);
    return new OperationResult(changedTarget, event);
  }

  private OperationResult handleSavingThrow(Subject source, Spell spell, Subject[] targets,
      Function<Subject, Integer> savingThrowSupplier, Function<Subject, OperationResult> failAction) {
    OperationResult operationResult = new OperationResult();
    for (Subject target : targets) {
      int savingThrow = savingThrowSupplier.apply(target);
      if (fightHelper.isSaved(source, savingThrow)) {
        OperationResult op = failAction.apply(target);
        operationResult.addAll(op.getChangedSubjects(), op.getEmittedEvents());
      } else {
        int attackDamage = spell.spellRoll();
        attackDamage += getModifier(source, spell);
        OperationResult op = dealDamage(source, target, attackDamage, spell);
        operationResult.addAll(op.getChangedSubjects(), op.getEmittedEvents());
      }
    }
    return operationResult;
  }

  private OperationResult dealDamage(Subject source, Subject target, int attackDamage, Spell spell) {
    int remainingHealthPoints = target.getCurrentHealthPoints() - attackDamage;
    Subject changedTarget = target.of(remainingHealthPoints);
    Event event = eventsFactory.successSpellCastEvent(source, changedTarget, spell);
    return new OperationResult(changedTarget, event);
  }

  private OperationResult applyEffect(Subject source, Spell spell, Subject target) {
    Subject changedTarget = target.of(spell.getSpellEffect());
    Event event = eventsFactory.successSpellCastEvent(source, changedTarget, spell);
    return new OperationResult(changedTarget, event);
  }

  private OperationResult heal(Subject source, Spell spell, Subject target) {
    int healRoll = spell.spellRoll();
    healRoll += getModifier(source, spell);
    Subject changedTarget = target.of(target.getCurrentHealthPoints() + healRoll);
    Event event = eventsFactory.successHealEvent(source, changedTarget);
    return new OperationResult(changedTarget, event);
  }

  @Override
  public int getAllowedNumberOfTargets(Instrument instrument) {
    return instrument.getSpell().getMaximumNumberOfTargets();
  }
}
