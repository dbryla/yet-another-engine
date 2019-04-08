package dbryla.game.yetanotherengine.domain.subjects.classes;

import dbryla.game.yetanotherengine.domain.Abilities;
import dbryla.game.yetanotherengine.domain.spells.Effect;
import dbryla.game.yetanotherengine.domain.state.SubjectIdentifier;
import dbryla.game.yetanotherengine.domain.subjects.ActiveEffect;
import dbryla.game.yetanotherengine.domain.subjects.IncorrectAttributesException;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Equipment;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Weapon;

public class Wizard extends BaseClass implements Subject {

  private static final int DEFAULT_WIZARD_HP = 6;

  private Wizard(Wizard oldState, int healthPoints) {
    this(oldState.id, oldState.maxHealthPoints, healthPoints, oldState.equipment, oldState.abilities, oldState.activeEffect);
  }

  private Wizard(SubjectIdentifier id, int maxHealthPoints, int currentHealthPoints, Equipment equipment,
      Abilities abilities, ActiveEffect activeEffect) {
    super(id, maxHealthPoints, currentHealthPoints, equipment, abilities, activeEffect);
  }

  private Wizard(Wizard oldState, ActiveEffect activeEffect) {
    this(oldState.id, oldState.maxHealthPoints, oldState.currentHealthPoints, oldState.equipment, oldState.abilities, activeEffect);
  }

  private Wizard(SubjectIdentifier id, int healthPoints, Equipment equipment, Abilities abilities) {
    this(id, healthPoints, healthPoints, equipment, abilities, null);
  }

  @Override
  public Subject of(int healthPoints) {
    return new Wizard(this, Math.min(healthPoints, this.maxHealthPoints));
  }

  @Override
  public Subject of(Effect effect) {
    return new Wizard(this, effect.activate());
  }

  @Override
  public Subject effectExpired() {
    return new Wizard(this, null);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private String name;
    private String affiliation;
    private Weapon weapon;
    private Abilities abilities;

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder affiliation(String affiliation) {
      this.affiliation = affiliation;
      return this;
    }

    public Builder weapon(Weapon weapon) {
      this.weapon = weapon;
      return this;
    }

    public Builder abilities(Abilities abilities) {
      this.abilities = abilities;
      return this;
    }

    public Wizard build() throws IncorrectAttributesException {
      SubjectIdentifier id = buildIdentifier(name, affiliation);
      Equipment equipment = new Equipment(weapon);
      return new Wizard(id, DEFAULT_WIZARD_HP + abilities.getConstitutionModifier(), equipment, abilities);
    }
  }

}
