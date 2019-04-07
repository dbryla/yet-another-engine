package dbryla.game.yetanotherengine.domain.subjects.classes;

import dbryla.game.yetanotherengine.domain.Abilities;
import dbryla.game.yetanotherengine.domain.spells.Effect;
import dbryla.game.yetanotherengine.domain.state.SubjectIdentifier;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Equipment;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Weapon;
import lombok.ToString;

@ToString
public class Fighter extends BaseClass implements Subject {

  private static final int DEFAULT_FIGHTER_HP = 10;

  private Fighter(Fighter oldState, int healthPoints) {
    this(oldState.id, healthPoints, oldState.equipment, oldState.abilities, oldState.activeEffect);
  }

  private Fighter(Fighter oldState, ActiveEffect activeEffect) {
    this(oldState.id, oldState.healthPoints, oldState.equipment, oldState.abilities, activeEffect);
  }

  private Fighter(SubjectIdentifier id,
                  int healthPoints,
                  Equipment equipment,
                  Abilities abilities,
                  ActiveEffect activeEffect) {
    super(id, healthPoints, equipment, abilities, activeEffect);
  }

  private Fighter(SubjectIdentifier id, int healthPoints, Equipment equipment, Abilities abilities) {
    this(id, healthPoints, equipment, abilities, null);
  }

  @Override
  public Subject of(int healthPoints) {
    return new Fighter(this, healthPoints);
  }

  @Override
  public Subject of(Effect effect) {
    return new Fighter(this, effect.activate());
  }

  @Override
  public Subject effectExpired() {
    return new Fighter(this, null);
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

    public Fighter build() throws IncorrectAttributesException {
      SubjectIdentifier id = buildIdentifier(name, affiliation);
      Equipment equipment = new Equipment(weapon, shield, armor);
      return new Fighter(id, DEFAULT_FIGHTER_HP + abilities.getConstitutionModifier(), equipment, abilities);
    }
  }

}
