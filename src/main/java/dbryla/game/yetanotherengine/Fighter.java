package dbryla.game.yetanotherengine;

import lombok.ToString;

@ToString
public class Fighter implements Subject {

  private final String name;
  private int healthPoints = 10;
  private final String affiliation;

  public Fighter(String name, String affiliation) {
    this.name = name;
    this.affiliation = affiliation;
  }

  private Fighter(String name, String affiliation, int healthPoints) {
    this.name = name;
    this.healthPoints = healthPoints;
    this.affiliation = affiliation;
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
    return new Fighter(this.name, this.affiliation, healthPoints);
  }

  @Override
  public String getAffiliation() {
    return affiliation;
  }

  @Override
  public SubjectIdenitifier toIdentifier() {
    return new SubjectIdenitifier(name, affiliation);
  }

  public int calculateAttackDamage() {
    return DiceRoll.k6();
  }

  public int calculateHitRoll() {
    return DiceRoll.k20() + 3;
  }
}
