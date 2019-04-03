package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.events.Event;
import dbryla.game.yetanotherengine.domain.events.EventLog;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import java.util.Collections;
import java.util.Set;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AttackOperation implements Operation<Subject, Subject> {

  private final EventLog eventLog;

  @Override
  public Set<Subject> invoke(Subject source, Subject... targets) throws UnsupportedGameOperationException {
    verifyParams(source, targets);
    Subject target = targets[0];
    int armorClass = target.getArmorClass();
    int hitRoll = source.calculateWeaponHitRoll();
    if (hitRoll < armorClass) {
      eventLog.send(Event.fail(source.getName(), target.getName()));
      return Collections.emptySet();
    }
    int remainingHealthPoints = target.getHealthPoints() - source.calculateAttackDamage();
    eventLog.send(Event.success(source.getName(), target.getName(), remainingHealthPoints <= 0, source.getWeapon()));
    return Set.of(target.of(remainingHealthPoints));
  }

  private void verifyParams(Subject source, Subject[] targets) throws UnsupportedAttackException {
    if (source == null) {
      throw new UnsupportedAttackException("Can't invoke operation on null source");
    }
    if (targets.length != 1) {
      throw new UnsupportedAttackException("Can't attack none or more than one target.");
    }
  }
}
