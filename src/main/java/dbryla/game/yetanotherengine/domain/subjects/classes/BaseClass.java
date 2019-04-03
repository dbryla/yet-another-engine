package dbryla.game.yetanotherengine.domain.subjects.classes;

import dbryla.game.yetanotherengine.domain.DiceRoll;
import dbryla.game.yetanotherengine.domain.spells.Effect;
import dbryla.game.yetanotherengine.domain.state.SubjectIdentifier;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.domain.subjects.Weapon;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class BaseClass implements Subject {

  protected static final int DEFAULT_ARMOR_CLASS = 10;
  protected final String name;
  protected final String affiliation;
  protected final int healthPoints;
  protected final int armorClass;
  protected final Weapon weapon;
  protected final Set<Effect> activeEffects = new HashSet<>();

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
  public int calculateAttackDamage() {
    if (weapon != null) {
      return weapon.rollAttackDamage();
    } else {
      return 1;
    }
  }

  @Override
  public int calculateWeaponHitRoll() {
    return DiceRoll.k20();
  }

  @Override
  public Weapon getWeapon() {
    return weapon;
  }

  protected void addNewEffect(Effect effect) {
    activeEffects.add(effect);
  }
}
