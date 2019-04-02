package dbryla.game.yetanotherengine.domain.subjects;

import dbryla.game.yetanotherengine.domain.DiceRoll;
import dbryla.game.yetanotherengine.domain.state.SubjectIdentifier;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
public class Fighter implements Subject {

  private final String name;
  private final String affiliation;
  private int healthPoints = 10;

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
  public SubjectIdentifier toIdentifier() {
    return new SubjectIdentifier(name, affiliation);
  }

  public int calculateAttackDamage() {
    return DiceRoll.k6();
  }

  public int calculateHitRoll() {
    return DiceRoll.k20() + 3;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private String name;
    private String affiliation;
    private int healthPoints;

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder affiliation(String affiliation) {
      this.affiliation = affiliation;
      return this;
    }

    public Builder healthPoints(int healthPoints) {
      this.healthPoints = healthPoints;
      return this;
    }

    public Fighter build() throws IncorrectAttributesException {
      if (name == null || affiliation == null) {
        throw new IncorrectAttributesException("Both name and affiliation attributes must be provided to builder.");
      }
      return new Fighter(name, affiliation, healthPoints);
    }

  }

}
