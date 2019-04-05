package dbryla.game.yetanotherengine.domain.subjects.classes;

import dbryla.game.yetanotherengine.domain.spells.Effect;
import dbryla.game.yetanotherengine.domain.state.SubjectIdentifier;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.domain.subjects.Weapon;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public abstract class BaseClass implements Subject {

  protected static final int DEFAULT_ARMOR_CLASS = 10;
  protected final String name;
  protected final String affiliation;
  protected final int healthPoints;
  protected final int armorClass;
  protected final Weapon weapon;
  protected final Effect activeEffect;
  protected int activeEffectDurationInTurns;

  @Override
  public int getInitiativeModifier() {
    return 0;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public boolean isTerminated() {
    return healthPoints <= 0;
  }

  @Override
  public int getHealthPoints() {
    return healthPoints;
  }

  @Override
  public int getArmorClass() {
    return armorClass;
  }

  @Override
  public String getAffiliation() {
    return affiliation;
  }

  @Override
  public SubjectIdentifier toIdentifier() {
    return new SubjectIdentifier(name, affiliation);
  }

  @Override
  public Weapon getWeapon() {
    return weapon;
  }

  @Override
  public Optional<Effect> getActiveEffect() {
    return Optional.ofNullable(activeEffect);
  }

  @Override
  public int getActiveEffectDurationInTurns() {
    return activeEffectDurationInTurns;
  }

  @Override
  public void decreaseDurationOfActiveEffect() {
    --activeEffectDurationInTurns;
  }
}
