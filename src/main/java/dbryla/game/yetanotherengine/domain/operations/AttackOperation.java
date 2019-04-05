package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.events.EventsFactory;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component("attackOperation")
public class AttackOperation implements Operation<Subject, Subject> {

  private static final int ALLOWED_NUMBER_OF_TARGETS = 1;
  private final EventHub eventHub;
  private final HitRollSupplier hitRollSupplier;
  private final EffectConsumer effectConsumer;
  private final EventsFactory eventsFactory;

  @Override
  public Set<Subject> invoke(Subject source, Subject... targets) throws UnsupportedGameOperationException {
    verifyParams(source, targets);
    Set<Subject> changes = new HashSet<>();
    Subject target = targets[0];
    int armorClass = target.getArmorClass();
    int hitRoll = hitRollSupplier.get(source, target);
    if (hitRoll < armorClass) {
      eventHub.send(eventsFactory.failEvent(source.getName(), target.getName()));
    } else {
      int remainingHealthPoints = target.getHealthPoints() - source.calculateAttackDamage();
      changes.add(target.of(remainingHealthPoints));
      eventHub.send(eventsFactory.successAttackEvent(source.getName(), target.getName(), remainingHealthPoints <= 0, source.getWeapon()));
    }
    effectConsumer.apply(source).ifPresent(changes::add);
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

  @Override
  public int getAllowedNumberOfTargets(Subject source) {
    return ALLOWED_NUMBER_OF_TARGETS;
  }


}
