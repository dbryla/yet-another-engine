package dbryla.game.yetanotherengine;

import java.util.Collections;
import java.util.Set;

public class AttackOperation implements Operation<Fighter, Subject> {

  private final EventLog eventLog;

  public AttackOperation(EventLog eventLog) {
    this.eventLog = eventLog;
  }

  @Override
  public Set<Subject> invoke(Fighter source, Subject... targets) throws UnsupportedGameOperationException {
    verifyParams(source, targets);
    Subject target = targets[0];
    int armorClass = target.getArmorClass();
    int hitRoll = source.calculateHitRoll();
    if (hitRoll < armorClass) {
      eventLog.send(AttackEvent.fail(source.getName(), target.getName()));
      return Collections.emptySet();
    }
    int remainingHealthPoints = target.getHealthPoints() - source.calculateAttackDamage();
    eventLog.send(AttackEvent.success(source.getName(), target.getName(), remainingHealthPoints <= 0));
    return Set.of(target.of(remainingHealthPoints));
  }

  private void verifyParams(Fighter source, Subject[] targets) throws UnsupportedAttackException {
    if (source == null) {
      throw new UnsupportedAttackException("Can't invoke operation on null source");
    }
    if (targets.length != 1) {
      throw new UnsupportedAttackException("Can't attack none or more than one target.");
    }
  }
}