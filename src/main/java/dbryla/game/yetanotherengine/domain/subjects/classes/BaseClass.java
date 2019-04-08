package dbryla.game.yetanotherengine.domain.subjects.classes;

import dbryla.game.yetanotherengine.domain.Abilities;
import dbryla.game.yetanotherengine.domain.state.SubjectIdentifier;
import dbryla.game.yetanotherengine.domain.subjects.ActiveEffect;
import dbryla.game.yetanotherengine.domain.subjects.IncorrectAttributesException;
import dbryla.game.yetanotherengine.domain.subjects.State;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Equipment;

import java.util.Optional;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class BaseClass implements Subject {

  protected final SubjectIdentifier id;
  protected final int maxHealthPoints;
  protected final int currentHealthPoints;
  protected final Equipment equipment;
  protected final Abilities abilities;
  protected final ActiveEffect activeEffect;

  @Override
  public int getInitiativeModifier() {
    return abilities.getDexterityModifier();
  }

  @Override
  public String getName() {
    return id.getName();
  }

  @Override
  public boolean isTerminated() {
    return currentHealthPoints <= 0;
  }

  @Override
  public int getCurrentHealthPoints() {
    return currentHealthPoints;
  }

  @Override
  public State getSubjectState() {
    if (currentHealthPoints <= 0) {
      return State.TERMINATED;
    }
    if (currentHealthPoints == maxHealthPoints) {
      return State.NORMAL;
    }
    if (currentHealthPoints > Math.ceil(0.75 * maxHealthPoints)) {
      return State.LIGHTLY_WOUNDED;
    }
    if (currentHealthPoints > Math.ceil(0.50 * maxHealthPoints)) {
      return State.WOUNDED;
    }
    if (currentHealthPoints < Math.ceil(0.10 * maxHealthPoints) && currentHealthPoints < 10) {
      return State.DEATHS_DOOR;
    }
    return State.HEAVILY_WOUNDED;
  }

  @Override
  public int getArmorClass() {
    int modifier = equipment.getArmor()
        .map(Armor::getMaxDexterityBonus)
        .map(Optional::get)
        .map(maxDexBonus -> Math.min(maxDexBonus, abilities.getDexterityModifier()))
        .orElse(abilities.getDexterityModifier());
    return equipment.getArmorClass() + modifier;
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
  public Optional<ActiveEffect> getActiveEffect() {
    return Optional.ofNullable(activeEffect);
  }

  @Override
  public Abilities getAbilities() {
    return abilities;
  }

  @Override
  public Equipment getEquipment() {
    return equipment;
  }

  protected static SubjectIdentifier buildIdentifier(String name, String affiliation) {
    if (name == null || affiliation == null) {
      throw new IncorrectAttributesException("Both name and affiliation attributes must be provided to builder.");
    }
    return new SubjectIdentifier(name, affiliation);
  }

}
