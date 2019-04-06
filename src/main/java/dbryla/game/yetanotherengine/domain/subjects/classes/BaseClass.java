package dbryla.game.yetanotherengine.domain.subjects.classes;

import dbryla.game.yetanotherengine.domain.state.SubjectIdentifier;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Equipment;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Weapon;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public abstract class BaseClass implements Subject {

  protected final SubjectIdentifier id;
  protected final int healthPoints;
  protected final Equipment equipment;
  protected final ActiveEffect activeEffect;

  @Override
  public int getInitiativeModifier() {
    return 0;
  }

  @Override
  public String getName() {
    return id.getName();
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
    return equipment.getArmorClass();
  }

  @Override
  public String getAffiliation() {
    return id.getAffiliation();
  }

  @Override
  public SubjectIdentifier toIdentifier() {
    return id;
  }

  @Override
  public Weapon getWeapon() {
    return equipment.getWeapon();
  }

  @Override
  public Optional<ActiveEffect> getActiveEffect() {
    return Optional.ofNullable(activeEffect);
  }

  protected static SubjectIdentifier buildIdentifier(String name, String affiliation) {
    if (name == null || affiliation == null) {
      throw new IncorrectAttributesException("Both name and affiliation attributes must be provided to builder.");
    }
    return new SubjectIdentifier(name, affiliation);
  }

}
