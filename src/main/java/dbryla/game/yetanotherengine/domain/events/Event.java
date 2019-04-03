package dbryla.game.yetanotherengine.domain.events;

import static dbryla.game.yetanotherengine.domain.spells.SpellConstants.EFFECT;

import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.domain.subjects.Weapon;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@AllArgsConstructor
public class Event {

  private final String message;

  public static Event successAttack(String attacker, String target, boolean isTargetTerminated, Weapon weapon) {
    return new Event(success(attacker, target, isTargetTerminated, weapon.toString()));
  }

  public static Event successSpellCast(String attacker, String target, boolean isTargetTerminated, Spell spell) {
    String message = success(attacker, target, isTargetTerminated, spell.toString().replace("_", " "));
    if (EFFECT.equals(spell.getDamageType())) {
      message += " " + target + " is " + spell.getSpellEffect().toString().toLowerCase() + "ed";
    }
    return new Event(message);
  }

  private static String success(String attacker, String target, boolean isTargetTerminated, String weapon) {
    String message = attacker + " hits " + target + " with " + weapon.toLowerCase() + ".";
    if (isTargetTerminated) {
      message += " " + target + " drops on the ground.";
    }
    return message;
  }

  public static Event fail(String attacker, String target) {
    String message = attacker + " misses attack on " + target + ".";
    return new Event(message);
  }

  public static Event effectExpired(Subject source) {
    return new Event(source.getName() + " is no longer " + source.getActiveEffect().toString().toLowerCase() + "ed.");
  }

  @Override
  public String toString() {
    return message;
  }
}
