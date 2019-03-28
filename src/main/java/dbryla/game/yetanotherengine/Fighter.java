package dbryla.game.yetanotherengine;

import lombok.ToString;

@ToString
public class Fighter implements Subject {

  private final String name;
  private int healthPoints = 10;

  public Fighter(String name) {
    this.name = name;
  }

  private Fighter(String name, int healthPoints) {
    this.name = name;
    this.healthPoints = healthPoints;
  }

  @Override
  public int getInitiativeModifier() {
    return 0;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getHealthPoints() {
    return healthPoints;
  }

  @Override
  public boolean isTerminated() {
    return healthPoints <= 0;
  }

  @Override
  public int getArmorClass() {
    return 10;
  }

  @Override
  public Subject of(int healthPoints) {
    return new Fighter(this.name, healthPoints);
  }

  public int calculateAttackDamage() {
    return DiceRoll.k6();
  }

  public int calculateHitRoll() {
    return DiceRoll.k20() + 3;
  }
}
