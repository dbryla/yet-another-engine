package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.DiceRollModifier;
import dbryla.game.yetanotherengine.domain.events.Event;
import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.subjects.Subject;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class AttackOperation implements Operation<Subject, Subject> {

  private final EventHub eventHub;

  @Override
  public Set<Subject> invoke(Subject source, Subject... targets) throws UnsupportedGameOperationException {
    verifyParams(source, targets);
    Set<Subject> changes = new HashSet<>();
    Subject target = targets[0];
    int armorClass = target.getArmorClass();
    int hitRoll = getHitRoll(source, target);
    if (hitRoll < armorClass) {
      eventHub.send(Event.fail(source.getName(), target.getName()));
    } else {
      int remainingHealthPoints = target.getHealthPoints() - source.calculateAttackDamage();
      changes.add(target.of(remainingHealthPoints));
      eventHub.send(Event.successAttack(source.getName(), target.getName(), remainingHealthPoints <= 0, source.getWeapon()));
    }
    decreaseEffectDuration(source).ifPresent(changes::add);
    return changes;
  }

  private void verifyParams(Subject source, Subject[] targets) throws UnsupportedAttackException {
    if (source == null) {
      throw new UnsupportedAttackException("Can't invoke operation on null source");
    }
    if (targets.length != 1) {
      throw new UnsupportedAttackException("Can't attack none or more than one target.");
    }
  }

  private int getHitRoll(Subject source, Subject target) {
    if (source.getActiveEffect() != null) {
      DiceRollModifier sourceRollModifier = source.getActiveEffect().getOwnerAsASource();
      if (sourceRollModifier.equals(DiceRollModifier.DISADVANTAGE)) {
        return sourceRollModifier.getDiceRollModifier();
      }
    }
    if (target.getActiveEffect() != null) {
      return target.getActiveEffect().getOwnerAsATarget().getDiceRollModifier();
    }
    return source.calculateWeaponHitRoll();
  }

  private Optional<Subject> decreaseEffectDuration(Subject source) {
    if (source.getActiveEffect() != null) {
      source.decreaseDurationOfActiveEffect();
      if (source.getActiveEffectDurationInTurns() == 0) {
        eventHub.send(Event.effectExpired(source));
        return Optional.of(source.effectExpired());
      }
    }
    return Optional.empty();
  }
}
