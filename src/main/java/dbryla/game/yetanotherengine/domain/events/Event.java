package dbryla.game.yetanotherengine.domain.events;

import static dbryla.game.yetanotherengine.domain.spells.SpellConstants.EFFECT;

import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subjects.Weapon;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Event {

  private final String message;

  public Event(String message) {
    this.message = message;
  }

  public static Event success(String attacker, String target, boolean isTargetTerminated, Weapon weapon) {
    return new Event(success(attacker, target, isTargetTerminated, weapon.toString()));
  }

  public static Event success(String attacker, String target, boolean isTargetTerminated, Spell spell) {
    String message = success(attacker, target, isTargetTerminated, spell.toString().replace("_", " "));
    if (EFFECT.equals(spell.getDamageType())) {
      message += " " + target + " is" + spell.getSpellEffect().toString().toLowerCase() + "ed";
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

  @Override
  public String toString() {
    return message;
  }
}
