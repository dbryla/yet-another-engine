package dbryla.game.yetanotherengine.domain.subject;

import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.effects.Effect;
import dbryla.game.yetanotherengine.domain.equipment.Weapon;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class State {

  private final String subjectName;
  private final int maxHealthPoints;
  private final int currentHealthPoints;
  private final Position position;
  private final Set<Condition> conditions;
  private final Weapon equippedWeapon;

  public boolean isTerminated() {
    return currentHealthPoints <= 0;
  }

  public State of(int healthPoints) {
    return new State(this.subjectName, this.maxHealthPoints, healthPoints, this.position, this.conditions, this.equippedWeapon);
  }

  public State of(Condition effect) {
    Set<Condition> newConditions = new HashSet<>(this.getConditions());
    newConditions.add(effect);
    return new State(this.subjectName, this.maxHealthPoints, this.currentHealthPoints, this.position, newConditions, this.equippedWeapon);
  }

  public State effectExpired(Effect effect) {
    Set<Condition> newConditions = new HashSet<>(this.getConditions());
    newConditions.removeIf(activeEffect -> activeEffect.getEffect().equals(effect));
    return new State(this.subjectName, this.maxHealthPoints, this.currentHealthPoints, this.position, newConditions, this.equippedWeapon);
  }

  public State of(Position newPosition) {
    return new State(this.subjectName, this.maxHealthPoints, this.currentHealthPoints, newPosition, this.conditions, this.equippedWeapon);
  }

  public State of(Weapon equippedWeapon) {
    return new State(this.subjectName, this.maxHealthPoints, this.currentHealthPoints, this.position, this.conditions, equippedWeapon);
  }

  public HealthState getHealthState() {
    if (isTerminated()) {
      return HealthState.TERMINATED;
    }
    if (currentHealthPoints == maxHealthPoints) {
      return HealthState.NORMAL;
    }
    if (currentHealthPoints >= Math.ceil(0.75 * maxHealthPoints)) {
      return HealthState.LIGHTLY_WOUNDED;
    }
    if (currentHealthPoints >= Math.ceil(0.50 * maxHealthPoints)) {
      return HealthState.WOUNDED;
    }
    if (currentHealthPoints <= Math.ceil(0.10 * maxHealthPoints) && currentHealthPoints < 10) {
      return HealthState.DEATHS_DOOR;
    }
    return HealthState.HEAVILY_WOUNDED;
  }

}
