package dbryla.game.yetanotherengine.domain.subjects.classes;

import dbryla.game.yetanotherengine.domain.spells.Effect;
import dbryla.game.yetanotherengine.domain.subjects.IncorrectAttributesException;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Weapon;
import lombok.ToString;

@ToString
public class Fighter extends BaseClass implements Subject {

  private static final int DEFAULT_FIGHTER_HP = 10;

  private final Armor shield;
  private final Armor armor;

  private Fighter(String name,
                  String affiliation,
                  int healthPoints,
                  Weapon weapon,
                  Armor shield,
                  Armor armor,
                  Effect effect,
                  int durationInTurns) {
    super(name, affiliation, healthPoints, weapon, effect, durationInTurns);
    this.shield = shield;
    this.armor = armor;
  }

  private Fighter(String name, String affiliation, int healthPoints, Weapon weapon, Armor shield, Armor armor) {
    this(name, affiliation, healthPoints, weapon, shield, armor, null, 0);
  }

  @Override
  public Subject of(int healthPoints) {
    return new Fighter(this.name,
        this.affiliation,
        healthPoints,
        this.weapon,
        this.shield,
        this.armor,
        this.activeEffect,
        this.activeEffectDurationInTurns);
  }

  @Override
  public Subject of(Effect effect) {
    return new Fighter(this.name,
        this.affiliation,
        this.healthPoints,
        this.weapon,
        this.shield,
        this.armor,
        effect,
        effect.getDurationInTurns());
  }

  @Override
  public Subject effectExpired() {
    return new Fighter(this.name, this.affiliation, healthPoints, this.weapon, this.shield, this.armor);
  }

  @Override
  public int getArmorClass() {
    int ac = DEFAULT_ARMOR_CLASS;
    if (shield != null) {
      ac += shield.getArmorClass();
    }
    if (armor != null) {
      ac += armor.getArmorClass();
    }
    return ac;
  }


  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private String name;
    private String affiliation;
    private int healthPoints = DEFAULT_FIGHTER_HP;
    private Weapon weapon;
    private Armor shield;
    private Armor armor;

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

    public Fighter build() throws IncorrectAttributesException {
      if (name == null || affiliation == null) {
        throw new IncorrectAttributesException("Both name and affiliation attributes must be provided to builder.");
      }
      return new Fighter(name, affiliation, healthPoints, weapon, shield, armor);
    }
  }

}
