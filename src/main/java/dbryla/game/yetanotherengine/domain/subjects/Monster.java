package dbryla.game.yetanotherengine.domain.subjects;

import dbryla.game.yetanotherengine.domain.Abilities;
import dbryla.game.yetanotherengine.domain.spells.Effect;
import dbryla.game.yetanotherengine.domain.state.SubjectIdentifier;
import dbryla.game.yetanotherengine.domain.subjects.classes.BaseClass;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Equipment;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Weapon;

import static dbryla.game.yetanotherengine.domain.GameOptions.ENEMIES;

public class Monster extends BaseClass implements Subject {

  private Monster(SubjectIdentifier id, int maxHealthPoints, int currentHealthPoints,
                 Equipment equipment, Abilities abilities, ActiveEffect activeEffect) {
    super(id, maxHealthPoints, currentHealthPoints, equipment, abilities, activeEffect);
  }

  private Monster(SubjectIdentifier id, int maxHealthPoints, Equipment equipment, Abilities abilities) {
    this(id, maxHealthPoints, maxHealthPoints, equipment, abilities, null);
  }

  @Override
  public Subject of(int healthPoints) {
    return new Monster(this.id, this.maxHealthPoints, healthPoints, this.equipment, this.abilities, this.activeEffect);
  }

  @Override
  public Subject of(Effect effect) {
    return new Monster(this.id, this.maxHealthPoints, this.currentHealthPoints,
        this.equipment, this.abilities, effect.activate());
  }

  @Override
  public Subject effectExpired() {
    return new Monster(this.id, this.maxHealthPoints, this.currentHealthPoints,
        this.equipment, this.abilities, null);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private String name;
    private int healthPoints;
    private Weapon weapon;
    private Armor shield;
    private Armor armor;
    private Abilities abilities;

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder healthPoints(int healthPoints) {
      this.healthPoints = healthPoints;
      return this;
    }

    public Builder weapon(Weapon weapon) {
      this.weapon = weapon;
      return this;
    }

    public Builder shield(Armor shield) {
      this.shield = shield;
      return this;
    }

    public Builder armor(Armor armor) {
      this.armor = armor;
      return this;
    }

    public Builder abilities(Abilities abilities) {
      this.abilities = abilities;
      return this;
    }

    public Monster build() {
      SubjectIdentifier id = buildIdentifier(name, ENEMIES);
      Equipment equipment = new Equipment(weapon, shield, armor);
      return new Monster(id, healthPoints + abilities.getConstitutionModifier(), equipment, abilities);
    }
  }
}
