package dbryla.game.yetanotherengine.domain.subjects.classes;

import dbryla.game.yetanotherengine.domain.spells.Effect;
import dbryla.game.yetanotherengine.domain.subjects.IncorrectAttributesException;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.domain.subjects.Weapon;
import lombok.Getter;

public class Mage extends BaseClass implements Subject {

  private static final int DEFAULT_MAGE_HP = 6;
  @Getter
  private final Spell spell;

  public Mage(String name, String affiliation, int healthPoints, int armorClass, Weapon weapon, Spell spell) {
    super(name, affiliation, healthPoints, armorClass, weapon);
    this.spell = spell;
  }

  public Mage(String name, String affiliation, int healthPoints, int armorClass, Weapon weapon, Spell spell, Effect... effects) {
    super(name, affiliation, healthPoints, armorClass, weapon);
    this.spell = spell;
    for (Effect effect : effects) {
      addNewEffect(effect);
    }
  }

  @Override
  public Subject of(int healthPoints) {
    return new Mage(this.name, this.affiliation, healthPoints, this.armorClass, this.weapon, this.spell);
  }

  @Override
  public Subject of(Effect effect) {
    return new Mage(this.name, this.affiliation, healthPoints, this.armorClass, this.weapon, this.spell, effect);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private String name;
    private String affiliation;
    private int healthPoints = DEFAULT_MAGE_HP;
    private int armorClass = DEFAULT_ARMOR_CLASS;
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

    public Builder armorClass(int armorClass) {
      this.armorClass = armorClass;
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
      return new Mage(name, affiliation, healthPoints, armorClass, weapon, spell);
    }
  }

}
