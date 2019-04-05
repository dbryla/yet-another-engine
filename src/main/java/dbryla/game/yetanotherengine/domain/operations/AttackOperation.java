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
  private final FightHelper fightHelper;
  private final EffectConsumer effectConsumer;
  private final EventsFactory eventsFactory;

  @Override
  public Set<Subject> invoke(Subject source, Subject... targets) throws UnsupportedGameOperationException {
    verifyParams(source, targets);
    Set<Subject> changes = new HashSet<>();
    Subject target = targets[0];
    int armorClass = target.getArmorClass();
    int hitRoll = fightHelper.getHitRoll(source, target);
    if (fightHelper.isMiss(armorClass, hitRoll)) {
      eventHub.send(eventsFactory.failEvent(source, target));
    } else {
      int attackDamage = fightHelper.getAttackDamage(source.getWeapon().rollAttackDamage(), hitRoll);
      int remainingHealthPoints = target.getHealthPoints() - attackDamage;
      changes.add(target.of(remainingHealthPoints));
      eventHub.send(eventsFactory.successAttackEvent(source, target));
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
