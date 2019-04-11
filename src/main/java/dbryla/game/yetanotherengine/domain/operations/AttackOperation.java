package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.Abilities;
import dbryla.game.yetanotherengine.domain.Instrument;
import dbryla.game.yetanotherengine.domain.events.Event;
import dbryla.game.yetanotherengine.domain.events.EventsFactory;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Weapon;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component("attackOperation")
public class AttackOperation implements Operation {

  private static final int ALLOWED_NUMBER_OF_TARGETS = 1;
  private final FightHelper fightHelper;
  private final EffectConsumer effectConsumer;
  private final EventsFactory eventsFactory;

  @Override
  public OperationResult invoke(Subject source, Instrument instrument, Subject... targets) throws UnsupportedGameOperationException {
    verifyParams(source, instrument, targets);
    Set<Subject> changes = new HashSet<>();
    Set<Event> events = new HashSet<>();
    Subject target = targets[0];
    HitRoll hitRoll = fightHelper.getHitRoll(source, target);
    Weapon weapon = instrument.getWeapon();
    hitRoll.addModifier(getModifier(weapon, source.getAbilities()));
    HitResult hitResult = HitResult.of(hitRoll, target);
    if (!hitResult.isTargetHit()) {
      events.add(eventsFactory.failEvent(source, target, weapon.toString(), hitResult));
    } else {
      int attackDamage = fightHelper.getAttackDamage(weapon.rollAttackDamage(), hitResult) + getModifier(weapon, source.getAbilities());
      int remainingHealthPoints = target.getCurrentHealthPoints() - attackDamage;
      Subject changedTarget = target.of(remainingHealthPoints);
      changes.add(changedTarget);
      events.add(eventsFactory.successAttackEvent(source, changedTarget, weapon, hitResult));
    }
    OperationResult operationResult = effectConsumer.apply(source).orElseGet(OperationResult::new);
    return operationResult.addAll(changes, events);
  }

  private void verifyParams(Subject source, Instrument instrument, Subject[] targets) throws UnsupportedAttackException {
    if (source == null) {
      throw new UnsupportedAttackException("Can't invoke operation on null source");
    }
    if (targets.length != 1) {
      throw new UnsupportedAttackException("Can't attack none or more than one target.");
    }
    if (source.getEquipment() == null || source.getEquipment().getWeapon() == null) {
      throw new UnsupportedAttackException("Can't attack without weapon.");
    }
    if (!source.getEquipment().getWeapon().equals(instrument.getWeapon())) {
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

  @Override
  public int getAllowedNumberOfTargets(Instrument instrument) {
    return ALLOWED_NUMBER_OF_TARGETS;
  }


}
