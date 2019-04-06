package dbryla.game.yetanotherengine.domain.subjects.classes;

import dbryla.game.yetanotherengine.domain.spells.Effect;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.state.SubjectIdentifier;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Equipment;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Weapon;

public class Cleric extends BaseClass implements Subject, SpellCaster {

  private static final int DEFAULT_CLERIC_HP = 8;
  private final Spell spell;

  private Cleric(SubjectIdentifier id, int healthPoints, Equipment equipment, Spell spell) {
    this(id, healthPoints, equipment, spell, null);
  }

  private Cleric(SubjectIdentifier id, int healthPoints, Equipment equipment, Spell spell, ActiveEffect activeEffect) {
    super(id, healthPoints, equipment, activeEffect);
    this.spell = spell;
  }

  private Cleric(Cleric oldState, int healthPoints) {
    this(oldState.id, healthPoints, oldState.equipment, oldState.spell, oldState.activeEffect);
  }

  private Cleric(Cleric oldState, ActiveEffect activeEffect) {
    this(oldState.id, oldState.healthPoints, oldState.equipment, oldState.spell, activeEffect);
  }

  @Override
  public Subject of(int healthPoints) {
    return new Cleric(this, healthPoints);
  }

  @Override
  public Subject of(Effect effect) {
    return new Cleric(this, effect.activate());
  }

  @Override
  public Subject effectExpired() {
    return new Cleric(this, null);
  }

  @Override
  public Spell getSpell() {
    return spell;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private String name;
    private String affiliation;
    private int healthPoints = DEFAULT_CLERIC_HP;
    private Weapon weapon;
    private Armor shield;
    private Armor armor;
    private Spell spell;

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

    public Builder spell(Spell spell) {
      this.spell = spell;
      return this;
    }

    public Cleric build() throws IncorrectAttributesException {
      SubjectIdentifier id = buildIdentifier(name, affiliation);
      Equipment equipment = new Equipment(weapon, shield, armor);
      return new Cleric(id, healthPoints, equipment, spell);
    }
  }
}
