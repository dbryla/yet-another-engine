package dbryla.game.yetanotherengine.domain.subjects.classes;

import dbryla.game.yetanotherengine.domain.Abilities;
import dbryla.game.yetanotherengine.domain.spells.Effect;
import dbryla.game.yetanotherengine.domain.state.SubjectIdentifier;
import dbryla.game.yetanotherengine.domain.subjects.ActiveEffect;
import dbryla.game.yetanotherengine.domain.subjects.IncorrectAttributesException;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Equipment;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Weapon;

public class Cleric extends BaseClass implements Subject {

  private static final int DEFAULT_CLERIC_HP = 8;

  private Cleric(SubjectIdentifier id, int maxHealthPoints, Equipment equipment, Abilities abilities) {
    this(id, maxHealthPoints, maxHealthPoints, equipment, abilities, null);
  }

  private Cleric(SubjectIdentifier id, int maxHealthPoints, int currentHealthPoints, Equipment equipment,
                 Abilities abilities, ActiveEffect activeEffect) {
    super(id, maxHealthPoints, currentHealthPoints, equipment, abilities, activeEffect);
  }

  private Cleric(Cleric oldState, int healthPoints) {
    this(oldState.id, oldState.maxHealthPoints, healthPoints, oldState.equipment, oldState.abilities, oldState.activeEffect);
  }

  private Cleric(Cleric oldState, ActiveEffect activeEffect) {
    this(oldState.id, oldState.maxHealthPoints, oldState.currentHealthPoints, oldState.equipment, oldState.abilities, activeEffect);
  }

  @Override
  public Subject of(int healthPoints) {
    return new Cleric(this, Math.min(healthPoints, this.maxHealthPoints));
  }

  @Override
  public Subject of(Effect effect) {
    return new Cleric(this, effect.activate());
  }

  @Override
  public Subject effectExpired() {
    return new Cleric(this, null);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private String name;
    private String affiliation;
    private Weapon weapon;
    private Armor shield;
    private Armor armor;
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

    public Cleric build() throws IncorrectAttributesException {
      SubjectIdentifier id = buildIdentifier(name, affiliation);
      Equipment equipment = new Equipment(weapon, shield, armor);
      return new Cleric(id, DEFAULT_CLERIC_HP + abilities.getConstitutionModifier(), equipment, abilities);
    }
  }
}