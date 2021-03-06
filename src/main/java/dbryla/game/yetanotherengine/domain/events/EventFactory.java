package dbryla.game.yetanotherengine.domain.events;

import dbryla.game.yetanotherengine.domain.effects.Effect;
import dbryla.game.yetanotherengine.domain.operations.HitResult;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.HealthState;
import dbryla.game.yetanotherengine.domain.subject.State;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.equipment.Weapon;
import dbryla.game.yetanotherengine.domain.subject.SubjectProperties;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import static dbryla.game.yetanotherengine.domain.operations.HitResult.CRITICAL;
import static dbryla.game.yetanotherengine.domain.spells.SpellSaveType.ARMOR_CLASS;
import static dbryla.game.yetanotherengine.domain.spells.SpellType.EFFECT;

@Component
@AllArgsConstructor
public class EventFactory {

  private static final String SUCCESS_HIT_FORMAT = "%s %s hits %s with %s.";
  private static final String TERMINATION_FORMAT = " %s drops dead.";
  private static final String EFFECT_FORMAT = "%s hits %s with %s. %s is %sed.";
  private static final String SUCCESS_SPELL_HIT_FORMAT = "%s casts %s and hits %s.";
  private static final String SAVE_THROW_FORMAT = "%s casts %s, but fails to harm %s.";
  private static final String HEAL_FORMAT = "%s heals %s.";
  private static final String MOVEMENT_FORMAT = "%s moves to %s.";
  private static final String EQUIP_WEAPON_FORMAT = "%s equips %s.";
  private static final String KNOCKED_PRONE_FORMAT = "%s knocks %s prone.";
  private static final String TARGET_IMMUNE_FORMAT = "%s hits %s with %s, but target seems to be immune.";
  private static final String STAND_UP_FORMAT = "%s stands up.";

  public Event successAttackEvent(Subject attacker, State target, Weapon weapon, HitResult hitResult) {
    return new Event(
        successMessage(attacker.getName(), target, target.isTerminated(), weapon, hitResult));
  }

  private String successMessage(String attacker, State target, boolean isTargetTerminated, Weapon weapon, HitResult hitResult) {
    if (isTargetTerminated) {
      if (CRITICAL.equals(hitResult)) {
        return String.format(weapon.getCriticalHitMessage(), attacker, target.getSubjectName())
            + String.format(TERMINATION_FORMAT, target.getSubjectName());
      }
      return attackWithHitRoll(attacker, target.getSubjectName(), weapon.toString(), hitResult)
          + String.format(TERMINATION_FORMAT, target.getSubjectName());
    }
    return attackWithHitRoll(attacker, target.getSubjectName(), weapon.toString(), hitResult) + HealthState.getMessageFor(target);
  }

  private String attackWithHitRoll(String attacker, String target, String instrument, HitResult hitResult) {
    return String.format(SUCCESS_HIT_FORMAT, attacker, hitResult.getMessage(), target, instrument);
  }

  public Event successSpellCastEvent(Subject attacker, State target, Spell spell, HitResult hitResult) {
    if (target.isTerminated()) {
      if (ARMOR_CLASS.equals(spell.getSpellSaveType()) && CRITICAL.equals(hitResult)) {
        return new Event((String.format(spell.getCriticalHitMessage(), attacker.getName(), target.getSubjectName()))
            + String.format(TERMINATION_FORMAT, target.getSubjectName()));
      }
      return new Event(
          attackWithHitRoll(attacker.getName(), target.getSubjectName(), spell.toString(), hitResult)
              + String.format(TERMINATION_FORMAT, target.getSubjectName()));
    }
    return
        new Event(attackWithHitRoll(attacker.getName(), target.getSubjectName(), spell.toString(), hitResult)
            + HealthState.getMessageFor(target));
  }

  public Event successSpellCastEvent(Subject attacker, State target, Spell spell) {
    if (EFFECT.equals(spell.getSpellType())) {
      return new Event(String.format(EFFECT_FORMAT,
          attacker.getName(), target.getSubjectName(), spell.toString(),
          target.getSubjectName(), spell.getSpellEffect().toString()));
    }
    if (target.isTerminated()) {
      return new Event(
          spellCastWithSaveThrow(attacker.getName(), target.getSubjectName(), spell.toString())
              + String.format(TERMINATION_FORMAT, target.getSubjectName()));
    }
    return
        new Event(spellCastWithSaveThrow(attacker.getName(), target.getSubjectName(), spell.toString()) + HealthState.getMessageFor(target));
  }

  private String spellCastWithSaveThrow(String attacker, String target, String spell) {
    return String.format(SUCCESS_SPELL_HIT_FORMAT, attacker, spell, target);
  }

  public Event failEvent(Subject attacker, Subject target, String instrumentName, HitResult hitResult) {
    return new Event(String.format(hitResult.getMessage(), attacker.getName(), target.getName(), instrumentName));
  }

  public Event effectExpiredEvent(Subject source, Effect effect) {
    return new Event(source.getName() + " is no longer " + effect + ".");
  }

  public Event successHealEvent(Subject source, State target) {
    return new Event(String.format(HEAL_FORMAT, source.getName(), target.getSubjectName()) + HealthState.getMessageFor(target));
  }

  public Event failEventBySavingThrow(Subject source, Spell spell, Subject target) {
    return new Event(String.format(SAVE_THROW_FORMAT, source.getName(), spell.toString(), target.getName()));
  }

  public Event movementEvent(State source) {
    return new Event(String.format(MOVEMENT_FORMAT, source.getSubjectName(), source.getPosition()));
  }

  public Event equipWeaponEvent(State source) {
    return new Event(String.format(EQUIP_WEAPON_FORMAT, source.getSubjectName(), source.getEquippedWeapon()));
  }

  public Event successKnockedProneEvent(Subject source, Subject target) {
    return new Event(String.format(KNOCKED_PRONE_FORMAT, source.getName(), target.getName()));
  }

  public Event targetImmuneEvent(Subject source, Subject target, Spell spell) {
    return new Event(String.format(TARGET_IMMUNE_FORMAT, source.getName(), target.getName(), spell.toString()));
  }

  public Event targetImmuneEvent(Subject source, Subject target, Weapon weapon) {
    return new Event(String.format(TARGET_IMMUNE_FORMAT, source.getName(), target.getName(), weapon.toString()));
  }

  public Event standUpEvent(Subject subject) {
    return new Event(String.format(STAND_UP_FORMAT, subject.getName()));
  }
}
