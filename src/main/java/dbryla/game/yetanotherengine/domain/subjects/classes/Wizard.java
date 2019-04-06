package dbryla.game.yetanotherengine.domain.subjects.classes;

import dbryla.game.yetanotherengine.domain.spells.Effect;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.state.SubjectIdentifier;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Equipment;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Weapon;

public class Wizard extends BaseClass implements SpellCaster {

  private static final int DEFAULT_MAGE_HP = 6;
  private final Spell spell;

  private Wizard(Wizard oldState, int healthPoints) {
    this(oldState.id, healthPoints, oldState.equipment, oldState.spell, oldState.activeEffect);
  }

  private Wizard(SubjectIdentifier id, int healthPoints, Equipment equipment, Spell spell, ActiveEffect activeEffect) {
    super(id, healthPoints, equipment, activeEffect);
    this.spell = spell;
  }

  private Wizard(Wizard oldState, ActiveEffect activeEffect) {
    this(oldState.id, oldState.healthPoints, oldState.equipment, oldState.spell, activeEffect);
  }

  private Wizard(SubjectIdentifier id, int healthPoints, Equipment equipment, Spell spell) {
    this(id, healthPoints, equipment, spell, null);
  }

  @Override
  public Subject of(int healthPoints) {
    return new Wizard(this, healthPoints);
  }

  @Override
  public Subject of(Effect effect) {
    return new Wizard(this, effect.activate());
  }

  @Override
  public Subject effectExpired() {
    return new Wizard(this, null);
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
    private int healthPoints = DEFAULT_MAGE_HP;
    private Weapon weapon;
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

    public Builder spell(Spell spell) {
      this.spell = spell;
      return this;
    }

    public Wizard build() throws IncorrectAttributesException {
      SubjectIdentifier id = buildIdentifier(name, affiliation);
      Equipment equipment = new Equipment(weapon);
      return new Wizard(id, healthPoints, equipment, spell);
    }
  }

}
