package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.dice.DiceRollService;
import dbryla.game.yetanotherengine.domain.events.EventsFactory;
import dbryla.game.yetanotherengine.domain.subject.Abilities;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@AllArgsConstructor
@Component
public class AttackOperation {

  private final FightHelper fightHelper;
  private final EventsFactory eventsFactory;
  private final DiceRollService diceRollService;

  public OperationResult invoke(Subject source, ActionData actionData, Subject... targets) throws UnsupportedGameOperationException {
    verifyParams(source, actionData, targets);
    Subject target = targets[0];
    OperationResult operationResult = equipWeapon(source, actionData).orElseGet(OperationResult::new);
    HitRoll hitRoll = fightHelper.getHitRoll(source, target);
    Weapon weapon = actionData.getWeapon();
    hitRoll.addModifier(getModifier(weapon, source.getAbilities()));
    HitResult hitResult = HitResult.of(hitRoll, target);
    if (!hitResult.isTargetHit()) {
      operationResult.add(eventsFactory.failEvent(source, target, weapon.toString(), hitResult));
    } else {
      int attackDamage = fightHelper.getAttackDamage(weapon.rollAttackDamage(diceRollService), hitResult)
          + getModifier(weapon, source.getAbilities());
      Subject changedTarget = fightHelper.dealDamage(target, attackDamage);
      operationResult.add(changedTarget, eventsFactory.successAttackEvent(source, changedTarget, weapon, hitResult));
    }
    return operationResult;
  }

  private Optional<OperationResult> equipWeapon(Subject source, ActionData data) {
    if (source.getEquippedWeapon().equals(data.getWeapon())) {
      return Optional.empty();
    }
    Subject changedSubject = source.of(data.getWeapon());
    return Optional.of(new OperationResult(changedSubject, eventsFactory.equipWeaponEvent(changedSubject)));
  }

  private void verifyParams(Subject source, ActionData actionData, Subject[] targets) throws UnsupportedAttackException {
    if (source == null) {
      throw new UnsupportedAttackException("Can't invoke operation on null source");
    }
    if (targets.length != 1) {
      throw new UnsupportedAttackException("Can't attack none or more than one target.");
    }
    if (actionData.getWeapon() == null) {
      throw new UnsupportedAttackException("Can't attack without weapons.");
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
