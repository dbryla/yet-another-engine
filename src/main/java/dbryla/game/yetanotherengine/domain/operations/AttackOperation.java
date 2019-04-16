package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.dice.DiceRollService;
import dbryla.game.yetanotherengine.domain.subject.Abilities;
import dbryla.game.yetanotherengine.domain.events.Event;
import dbryla.game.yetanotherengine.domain.events.EventsFactory;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class AttackOperation {

  private final FightHelper fightHelper;
  private final EventsFactory eventsFactory;
  private final DiceRollService diceRollService;

  public OperationResult invoke(Subject source, ActionData actionData, Subject... targets) throws UnsupportedGameOperationException {
    verifyParams(source, actionData, targets);
    Set<Subject> changes = new HashSet<>();
    Set<Event> events = new HashSet<>();
    Subject target = targets[0];
    HitRoll hitRoll = fightHelper.getHitRoll(source, target);
    Weapon weapon = actionData.getWeapon();
    hitRoll.addModifier(getModifier(weapon, source.getAbilities()));
    HitResult hitResult = HitResult.of(hitRoll, target);
    if (!hitResult.isTargetHit()) {
      events.add(eventsFactory.failEvent(source, target, weapon.toString(), hitResult));
    } else {
      int attackDamage = fightHelper.getAttackDamage(weapon.rollAttackDamage(diceRollService), hitResult)
          + getModifier(weapon, source.getAbilities());
      Subject changedTarget = fightHelper.dealDamage(target, attackDamage);
      changes.add(changedTarget);
      events.add(eventsFactory.successAttackEvent(source, changedTarget, weapon, hitResult));
    }
    return new OperationResult(changes, events);
  }

  private void verifyParams(Subject source, ActionData actionData, Subject[] targets) throws UnsupportedAttackException {
    if (source == null) {
      throw new UnsupportedAttackException("Can't invoke operation on null source");
    }
    if (targets.length != 1) {
      throw new UnsupportedAttackException("Can't attack none or more than one target.");
    }
    if (source.getEquipment() == null || source.getEquipment().getWeapon() == null) {
      throw new UnsupportedAttackException("Can't attack without weapon.");
    }
    if (!source.getEquipment().getWeapon().equals(actionData.getWeapon())) {
      throw new UnsupportedAttackException("Can't attack with different weapon than equipped one.");
    }
  }

  private int getModifier(Weapon weapon, Abilities abilities) {
    return weapon.isMelee()
        ? getFinesseModifierIfApplicable(weapon, abilities)
        : abilities.getDexterityModifier();
  }

  private int getFinesseModifierIfApplicable(Weapon weapon, Abilities abilities) {
    return weapon.isFinesse() ? getFinesseModifier(abilities) : abilities.getStrengthModifier();
  }

  private int getFinesseModifier(Abilities abilities) {
    return Math.max(abilities.getStrengthModifier(), abilities.getDexterityModifier());
  }

}
