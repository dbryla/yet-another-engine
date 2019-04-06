package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.Instrument;
import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.events.EventsFactory;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.spells.SpellSaveType;
import dbryla.game.yetanotherengine.domain.subjects.classes.Subject;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import static dbryla.game.yetanotherengine.domain.spells.SpellConstants.*;
import static dbryla.game.yetanotherengine.domain.spells.SpellType.*;

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
      for (Subject target : targets) {
        int hitRoll = fightHelper.getHitRoll(source, target);
        if (fightHelper.isMiss(target.getArmorClass(), hitRoll)) {
          eventHub.send(eventsFactory.failEvent(source, target));
        } else {
          int attackDamage = fightHelper.getAttackDamage(spell.spellRoll(), hitRoll);
          changes.add(dealDamage(source, target, attackDamage, spell));
        }
      }
    }
    if (SpellSaveType.CONSTITUTION_SAVING_THROW.equals(spell.getSpellSaveType())) {
      for (Subject target : targets) {
        int savingThrow = fightHelper.getConstitutionSavingThrow(source, target);
        if (fightHelper.isSaved(savingThrow)) {
          eventHub.send(eventsFactory.failEvent(source, target));
        } else {
          int attackDamage = spell.spellRoll();
          changes.add(dealDamage(source, target, attackDamage, spell));
        }
      }
    }
    if (SpellSaveType.DEXTERITY_SAVING_THROW.equals(spell.getSpellSaveType())
        || SpellSaveType.DEXTERITY_HALF_SAVING_THROW.equals(spell.getSpellSaveType())) {
      for (Subject target : targets) {
        int savingThrow = fightHelper.getDexteritySavingThrow(source, target);
        if (fightHelper.isSaved(savingThrow)) {
          if (SpellSaveType.DEXTERITY_SAVING_THROW.equals(spell.getSpellSaveType())) {
            eventHub.send(eventsFactory.failEvent(source, target));
          } else {
            int attackDamage = spell.spellRoll() / 2;
            changes.add(dealDamage(source, target, attackDamage, spell));
          }
        } else {
          int attackDamage = spell.spellRoll();
          changes.add(dealDamage(source, target, attackDamage, spell));
        }
      }
    }
    return changes;
  }

  private Subject dealDamage(Subject source, Subject target, int attackDamage, Spell spell) {
    int remainingHealthPoints = target.getHealthPoints() - attackDamage;
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
    Subject changedTarget = target.of(target.getHealthPoints() + spell.spellRoll());
    eventHub.send(eventsFactory.successHealEvent(source, changedTarget));
    return changedTarget;
  }

  @Override
  public int getAllowedNumberOfTargets(Instrument instrument) {
    return instrument.getSpell().getMaximumNumberOfTargets();
  }
}
