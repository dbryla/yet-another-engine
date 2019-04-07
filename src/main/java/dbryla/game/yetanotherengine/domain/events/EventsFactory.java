package dbryla.game.yetanotherengine.domain.events;

import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subjects.classes.Subject;
import org.springframework.stereotype.Component;

import static dbryla.game.yetanotherengine.domain.spells.SpellType.EFFECT;

@Component
public class EventsFactory {

  public Event successAttackEvent(Subject attacker, Subject target) {
    return new Event(
        successMessage(attacker.getName(), target.getName(), target.isTerminated(), getWeaponName(attacker)));
  }

  private String getWeaponName(Subject attacker) {
    return attacker.getWeapon() != null ? attacker.getWeapon().toString() : "fists";
  }

  private String successMessage(String attacker, String target, boolean isTargetTerminated, String weapon) {
    String message = attacker + " hits " + target + " with " + weapon.toLowerCase() + ".";
    if (isTargetTerminated) {
      message += " " + target + " drops on the ground.";
    }
    return message;
  }

  public Event successSpellCastEvent(Subject attacker, Subject target, Spell spell) {
    String message = successMessage(attacker.getName(), target.getName(), target.isTerminated(), getSpellName(spell));
    if (EFFECT.equals(spell.getSpellType())) {
      message += " " + target.getName() + " is " + spell.getSpellEffect().toString().toLowerCase() + "ed.";
    }
    return new Event(message);
  }

  private String getSpellName(Spell spell) {
    return spell.toString().replace("_", " ");
  }

  public Event failEvent(Subject attacker, Subject target) {
    String message = attacker.getName() + " misses attack on " + target.getName() + ".";
    return new Event(message);
  }

  public Event effectExpiredEvent(Subject source) {
    return new Event(source.getName() + " is no longer " + getActiveEffectName(source) + "ed.");
  }

  private String getActiveEffectName(Subject source) {
    return source.getActiveEffect().get().toString().toLowerCase();
  }

  public Event successHealEvent(Subject source, Subject target) {
    return new Event(source.getName() + " heals " + target.getName() + ".");
  }
}
