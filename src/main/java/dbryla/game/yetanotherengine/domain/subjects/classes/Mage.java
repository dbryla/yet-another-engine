package dbryla.game.yetanotherengine.domain.subjects.classes;

import dbryla.game.yetanotherengine.domain.spells.Effect;
import dbryla.game.yetanotherengine.domain.subjects.IncorrectAttributesException;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Weapon;
import lombok.Getter;

public class Mage extends BaseClass implements Subject {

  private static final int DEFAULT_MAGE_HP = 6;
  @Getter
  private final Spell spell;

  private Mage(String name, String affiliation, int healthPoints, Weapon weapon, Spell spell) {
    this(name, affiliation, healthPoints, weapon, spell, null, 0);
  }

  private Mage(String name,
               String affiliation,
               int healthPoints,
               Weapon weapon,
               Spell spell,
               Effect effect,
               int effectDurationInTurns) {
    super(name, affiliation, healthPoints, weapon, effect, effectDurationInTurns);
    this.spell = spell;
  }

  @Override
  public Subject of(int healthPoints) {
    return new Mage(this.name,
        this.affiliation,
        healthPoints,
        this.weapon,
        this.spell,
        this.activeEffect,
        this.activeEffectDurationInTurns);
  }

  @Override
  public Subject of(Effect effect) {
    return new Mage(this.name,
        this.affiliation,
        healthPoints,
        this.weapon,
        this.spell,
        effect,
        effect.getDurationInTurns());
  }

  @Override
  public Subject effectExpired() {
    return new Mage(this.name, this.affiliation, healthPoints, this.weapon, this.spell);
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

    public Mage build() throws IncorrectAttributesException {
      if (name == null || affiliation == null) {
        throw new IncorrectAttributesException("Both name and affiliation attributes must be provided to builder.");
      }
      return new Mage(name, affiliation, healthPoints, weapon, spell);
    }
  }

}
