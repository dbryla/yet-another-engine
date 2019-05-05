package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.dice.DiceRollService;
import dbryla.game.yetanotherengine.domain.effects.Effect;
import dbryla.game.yetanotherengine.domain.events.EventFactory;
import dbryla.game.yetanotherengine.domain.subject.Abilities;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

import static dbryla.game.yetanotherengine.domain.effects.Effect.PARALYZED;
import static dbryla.game.yetanotherengine.domain.effects.Effect.UNCONSCIOUS;
import static dbryla.game.yetanotherengine.domain.subject.CharacterClass.ROGUE;
import static dbryla.game.yetanotherengine.domain.subject.Race.BEAST;

@AllArgsConstructor
@Component
public class AttackOperation implements Operation {

  public static final Set<Effect> PARALYZED_OR_UNCONSCIOUS = Set.of(PARALYZED, UNCONSCIOUS);
  private final FightHelper fightHelper;
  private final EventFactory eventFactory;
  private final DiceRollService diceRollService;

  @Override
  public OperationResult invoke(Subject source, ActionData actionData, Subject... targets) throws UnsupportedGameOperationException {
    verifyParams(source, actionData, targets);
    Subject target = targets[0];
    OperationResult operationResult = equipWeapon(source, actionData).orElseGet(OperationResult::new);
    Weapon weapon = actionData.getWeapon();
    HitRoll hitRoll = fightHelper.getHitRoll(source, weapon, target);
    hitRoll.addModifier(getModifier(weapon, source.getAbilities()));
    HitResult hitResult = getHitResult(weapon, target, hitRoll);
    if (!hitResult.isTargetHit()) {
      operationResult.add(eventFactory.failEvent(source, target, weapon.toString(), hitResult));
    } else {
      int attackDamage = fightHelper.getAttackDamage(source,
          () -> weapon.rollAttackDamage(diceRollService) + getBonusDamage(source, weapon), hitResult)
          + getModifier(weapon, source.getAbilities());
      if (attackDamage <= 0) {
        attackDamage = 1;
      }
      fightHelper.dealDamage(target, attackDamage, weapon.getDamageType())
          .ifPresentOrElse(changedTarget -> operationResult
                  .add(changedTarget, eventFactory.successAttackEvent(source, changedTarget, weapon, hitResult)),
              () -> operationResult.add(eventFactory.targetImmuneEvent(source, target, weapon)));
    }
    return operationResult;
  }

  private HitResult getHitResult(Weapon weapon, Subject target, HitRoll hitRoll) {
    if (weapon.isMelee()
        && target.getConditions().stream().anyMatch(condition -> PARALYZED_OR_UNCONSCIOUS.contains(condition.getEffect()))) {
      return HitResult.CRITICAL;
    }
    return HitResult.of(hitRoll, target);
  }

  private int getBonusDamage(Subject source, Weapon weapon) {
    if (ROGUE.equals(source.getCharacterClass()) && (weapon.isFinesse() || weapon.isRanged())) {
      return diceRollService.k6();
    }
    return 0;
  }

  private Optional<OperationResult> equipWeapon(Subject source, ActionData data) {
    if (source.getEquippedWeapon().equals(data.getWeapon()) || BEAST.equals(source.getRace())) {
      return Optional.empty();
    }
    Subject changedSubject = source.of(data.getWeapon());
    return Optional.of(new OperationResult(changedSubject, eventFactory.equipWeaponEvent(changedSubject)));
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
