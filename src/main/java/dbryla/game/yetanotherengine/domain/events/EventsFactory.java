package dbryla.game.yetanotherengine.domain.events;

import static dbryla.game.yetanotherengine.domain.spells.SpellConstants.EFFECT;

import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.domain.subjects.Weapon;
import org.springframework.stereotype.Component;

@Component
public class EventsFactory {

  public Event successAttackEvent(String attacker, String target, boolean isTargetTerminated, Weapon weapon) {
    return new Event(successMessage(attacker, target, isTargetTerminated, weapon.toString()));
  }

  public Event successSpellCastEvent(String attacker, String target, boolean isTargetTerminated, Spell spell) {
    String message = successMessage(attacker, target, isTargetTerminated, spell.toString().replace("_", " "));
    if (EFFECT.equals(spell.getDamageType())) {
      message += " " + target + " is " + spell.getSpellEffect().toString().toLowerCase() + "ed";
    }
    return new Event(message);
  }

  private String successMessage(String attacker, String target, boolean isTargetTerminated, String weapon) {
    String message = attacker + " hits " + target + " with " + weapon.toLowerCase() + ".";
    if (isTargetTerminated) {
      message += " " + target + " drops on the ground.";
    }
    return message;
  }

  public Event failEvent(String attacker, String target) {
    String message = attacker + " misses attack on " + target + ".";
    return new Event(message);
  }

  public Event effectExpiredEvent(Subject source) {
    return new Event(source.getName() + " is no longer " + source.getActiveEffect().toString().toLowerCase() + "ed.");
  }

}
