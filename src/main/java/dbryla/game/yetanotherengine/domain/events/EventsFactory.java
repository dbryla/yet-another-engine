package dbryla.game.yetanotherengine.domain.events;

import dbryla.game.yetanotherengine.domain.operations.HitFlavor;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subjects.classes.Subject;
import org.springframework.stereotype.Component;

import static dbryla.game.yetanotherengine.domain.spells.SpellType.EFFECT;

@Component
public class EventsFactory {

  private static final String SUCCESS_HIT_FORMAT = "%s %s hits %s with %s.";

  public Event successAttackEvent(Subject attacker, Subject target, HitFlavor hitFlavor) {
    return new Event(
        successMessage(attacker.getName(), target.getName(),
            target.isTerminated(), getWeaponName(attacker), hitFlavor));
  }

  private String getWeaponName(Subject attacker) {
    return attacker.getWeapon() != null ? format(attacker.getWeapon().toString()) : "fists";
  }

  private String format(String string) {
    return string.toLowerCase().replace("_", " ");
  }

  private String successMessage(String attacker, String target,
                                boolean isTargetTerminated, String weapon, HitFlavor hitFlavor) {
    String message = String.format(SUCCESS_HIT_FORMAT,
        format(hitFlavor.toString()), attacker, target, weapon.toLowerCase());
    if (isTargetTerminated) {
      message += " " + target + " drops on the ground.";
    }
    return message;
  }

  public Event successSpellCastEvent(Subject attacker, Subject target, Spell spell, HitFlavor hitFlavor) {
    String message
        = successMessage(attacker.getName(), target.getName(), target.isTerminated(), getSpellName(spell), hitFlavor);
    if (EFFECT.equals(spell.getSpellType())) {
      message += " " + target.getName() + " is " + spell.getSpellEffect().toString().toLowerCase() + "ed.";
    }
    return new Event(message);
  }

  private String getSpellName(Spell spell) {
    return format(spell.toString());
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
