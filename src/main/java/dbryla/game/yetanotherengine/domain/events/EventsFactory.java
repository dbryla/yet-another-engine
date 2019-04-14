package dbryla.game.yetanotherengine.domain.events;

import static dbryla.game.yetanotherengine.domain.operations.HitResult.CRITICAL;
import static dbryla.game.yetanotherengine.domain.spells.SpellSaveType.ARMOR_CLASS;
import static dbryla.game.yetanotherengine.domain.spells.SpellType.EFFECT;

import dbryla.game.yetanotherengine.domain.operations.HitResult;
import dbryla.game.yetanotherengine.domain.effects.Effect;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.State;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EventsFactory {

  private static final String SUCCESS_HIT_FORMAT = "%s %s hits %s with %s.";
  private static final String TERMINATION_FORMAT = " %s drops dead.";
  private static final String EFFECT_FORMAT = "%s hits %s with %s. %s is %sed.";
  private static final String SUCCESS_SPELL_HIT_FORMAT = "%s casts %s and hits %s.";
  private static final String SAVE_THROW_FORMAT = "%s casts %s, but fails to harm %s.";
  private static final String HEAL_FORMAT = "%s heals %s.";

  public Event successAttackEvent(Subject attacker, Subject target, Weapon weapon, HitResult hitResult) {
    return new Event(
        successMessage(attacker.getName(), target, target.isTerminated(), weapon, hitResult));
  }

  private String successMessage(String attacker, Subject target, boolean isTargetTerminated, Weapon weapon, HitResult hitResult) {
    if (isTargetTerminated) {
      if (CRITICAL.equals(hitResult)) {
        return String.format(weapon.getCriticalHitMessage(), attacker, target.getName())
            + String.format(TERMINATION_FORMAT, target.getName());
      }
      return attackWithHitRoll(attacker, target.getName(), weapon.toString(), hitResult) + String.format(TERMINATION_FORMAT, target.getName());
    }
    return attackWithHitRoll(attacker, target.getName(), weapon.toString(), hitResult) + State.getMessageFor(target);
  }

  private String attackWithHitRoll(String attacker, String target, String instrument, HitResult hitResult) {
    return String.format(SUCCESS_HIT_FORMAT, attacker, hitResult.getMessage(), target, instrument);
  }

  public Event successSpellCastEvent(Subject attacker, Subject target, Spell spell, HitResult hitResult) {
    if (target.isTerminated()) {
      if (ARMOR_CLASS.equals(spell.getSpellSaveType()) && CRITICAL.equals(hitResult)) {
        return new Event((String.format(spell.getCriticalHitMessage(), attacker, target))
            + String.format(TERMINATION_FORMAT, target.getName()));
      }
      return new Event(
          attackWithHitRoll(attacker.getName(), target.getName(), spell.toString(), hitResult)
              + String.format(TERMINATION_FORMAT, target.getName()));
    }
    return new Event(attackWithHitRoll(attacker.getName(), target.getName(), spell.toString(), hitResult) + State.getMessageFor(target));
  }

  public Event successSpellCastEvent(Subject attacker, Subject target, Spell spell) {
    if (EFFECT.equals(spell.getSpellType())) {
      return new Event(String.format(EFFECT_FORMAT,
          attacker.getName(), target.getName(), spell.toString(), target.getName(), spell.getSpellEffect().toString()));
    }
    if (target.isTerminated()) {
      return new Event(
          spellCastWithSaveThrow(attacker.getName(), target.getName(), spell.toString()) + String.format(TERMINATION_FORMAT, target.getName()));
    }
    return new Event(spellCastWithSaveThrow(attacker.getName(), target.getName(), spell.toString()) + State.getMessageFor(target));
  }

  private String spellCastWithSaveThrow(String attacker, String target, String spell) {
    return String.format(SUCCESS_SPELL_HIT_FORMAT, attacker, spell, target);
  }

  public Event failEvent(Subject attacker, Subject target, String instrumentName, HitResult hitResult) {
    return new Event(String.format(hitResult.getMessage(), attacker.getName(), target.getName(), instrumentName));
  }

  public Event effectExpiredEvent(Subject source, Effect effect) {
    return new Event(source.getName() + " is no longer " + effect + "ed.");
  }

  public Event successHealEvent(Subject source, Subject target) {
    return new Event(String.format(HEAL_FORMAT, source.getName(), target.getName()) + State.getMessageFor(target));
  }

  public Event failEventBySavingThrow(Subject source, Spell spell, Subject target) {
    return new Event(String.format(SAVE_THROW_FORMAT, source.getName(), spell.toString(), target.getName()));
  }
}
