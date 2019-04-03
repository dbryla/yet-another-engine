package dbryla.game.yetanotherengine.domain.subjects.classes;

import dbryla.game.yetanotherengine.domain.spells.Effect;
import dbryla.game.yetanotherengine.domain.subjects.IncorrectAttributesException;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.domain.subjects.Weapon;
import lombok.ToString;

@ToString
public class Fighter extends BaseClass implements Subject {

  private static final int DEFAULT_FIGHTER_HP = 10;

  public Fighter(String name, String affiliation) {
    super(name, affiliation, DEFAULT_FIGHTER_HP, DEFAULT_ARMOR_CLASS, Weapon.SHORTSWORD);
  }

  public Fighter(String name, String affiliation, int healthPoints, int armorClass, Weapon weapon, Effect... effects) {
    super(name, affiliation, healthPoints, armorClass, weapon);
    for (Effect effect: effects) {
      addNewEffect(effect);
    }
  }

  @Override
  public Subject of(int healthPoints) {
    return new Fighter(this.name, this.affiliation, healthPoints, this.armorClass, this.weapon);
  }

  @Override
  public Subject of(Effect effect) {
    return new Fighter(this.name, this.affiliation, healthPoints, this.armorClass, this.weapon, effect);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private String name;
    private String affiliation;
    private int healthPoints = DEFAULT_FIGHTER_HP;
    private int armorClass = DEFAULT_ARMOR_CLASS;
    private Weapon weapon;

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

    public Fighter build() throws IncorrectAttributesException {
      if (name == null || affiliation == null) {
        throw new IncorrectAttributesException("Both name and affiliation attributes must be provided to builder.");
      }
      return new Fighter(name, affiliation, healthPoints, armorClass, weapon);
    }
  }

}
