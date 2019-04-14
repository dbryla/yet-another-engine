package dbryla.game.yetanotherengine.domain.subject;

import dbryla.game.yetanotherengine.domain.game.state.SubjectIdentifier;
import dbryla.game.yetanotherengine.domain.effects.Effect;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subject.equipment.Equipment;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
public class Subject {

  private final SubjectIdentifier id;
  @Getter
  private final Race race;
  @Getter
  private final CharacterClass characterClass;
  @Getter
  private final int maxHealthPoints;
  @Getter
  private int currentHealthPoints;
  @Getter
  private final Equipment equipment;
  @Getter
  private final Abilities abilities;
  @Getter
  private final List<Spell> spells;
  @Getter
  private final Set<ActiveEffect> activeEffects;

  /**
   * Used to create new subject object with given properties.
   */
  public Subject(String name, Race race, CharacterClass characterClass, String affiliation, Abilities abilities, Weapon weapon,
                 Armor armor, Armor shield, List<Spell> spells, int maxHealthPoints) {
    this(new SubjectIdentifier(name, affiliation), race, characterClass, maxHealthPoints, new Equipment(weapon, shield, armor),
        abilities, spells);
  }

  private Subject(SubjectIdentifier id, Race race, CharacterClass characterClass, int maxHealthPoints,
                  Equipment equipment, Abilities abilities, List<Spell> spells) {
    this(id, race, characterClass, maxHealthPoints, maxHealthPoints, equipment, abilities, spells, Set.of());
  }

  public int getInitiativeModifier() {
    return abilities.getDexterityModifier();
  }

  public String getName() {
    return id.getName();
  }

  public boolean isTerminated() {
    return currentHealthPoints <= 0;
  }

  public State getSubjectState() {
    if (currentHealthPoints <= 0) {
      return State.TERMINATED;
    }
    if (currentHealthPoints == maxHealthPoints) {
      return State.NORMAL;
    }
    if (currentHealthPoints > Math.ceil(0.75 * maxHealthPoints)) {
      return State.LIGHTLY_WOUNDED;
    }
    if (currentHealthPoints > Math.ceil(0.50 * maxHealthPoints)) {
      return State.WOUNDED;
    }
    if (currentHealthPoints <= Math.ceil(0.10 * maxHealthPoints) && currentHealthPoints < 10) {
      return State.DEATHS_DOOR;
    }
    return State.HEAVILY_WOUNDED;
  }

  public int getArmorClass() {
    Integer modifier = abilities.getDexterityModifier();
    if (equipment.getArmor().isPresent()) {
      modifier = equipment.getArmor()
          .get()
          .getMaxDexterityBonus()
          .map(maxDexBonus -> Math.min(maxDexBonus, abilities.getDexterityModifier()))
          .orElse(abilities.getDexterityModifier());
    }
    return equipment.getArmorClass() + modifier;
  }

  public String getAffiliation() {
    return id.getAffiliation();
  }

  public SubjectIdentifier toIdentifier() {
    return id;
  }

  public boolean isSpellCaster() {
    return !spells.isEmpty() || characterClass.isSpellCaster();
  }

  public Subject of(int healthPoints) {
    this.currentHealthPoints = healthPoints;
    return this;
  }

  public Subject of(Effect effect) {
    this.getActiveEffects().add(effect.activate());
    return this;
  }

  public Subject effectExpired(Effect effect) {
    this.getActiveEffects().removeIf(activeEffect -> activeEffect.getEffect().equals(effect));
    return this;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(race + " " + characterClass + "\n"
        + " HP:" + currentHealthPoints + "/" + maxHealthPoints + " AC:" + getArmorClass() + "\n"
        + abilities + "\n"
        + "Equipment:\n"
        + "- " + equipment.getWeapon() + "\n");
    equipment.getArmor().map(Armor::toString).ifPresent(armor -> stringBuilder.append("- ").append(armor).append("\n"));
    equipment.getShield().map(Armor::toString).ifPresent(shield -> stringBuilder.append("- ").append(shield).append("\n"));
    if (spells != null) {
      stringBuilder.append("Additional spells:\n");
      spells.forEach(spell -> stringBuilder.append("- ").append(spell).append("\n"));
    }
    return stringBuilder.toString();
  }

  /**
   * Used to create new subject object, will calculate values for all properties.
   *
   * @return builder for subject object
   */
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private String name;
    private String affiliation;
    private CharacterClass characterClass;
    private Race race;
    private Abilities abilities;
    private Weapon weapon;
    private Armor shield;
    private Armor armor;
    private List<Spell> spells;
    private int healthPoints;

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder affiliation(String affiliation) {
      this.affiliation = affiliation;
      return this;
    }

    public Builder characterClass(CharacterClass characterClass) {
      this.characterClass = characterClass;
      return this;
    }

    public Builder race(Race race) {
      this.race = race;
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

    public Builder spells(List<Spell> spells) {
      this.spells = spells;
      return this;
    }

    /**
     * Replaces default class health points
     */
    public Builder healthPoints(int healthPoints) {
      this.healthPoints = healthPoints;
      return this;
    }

    public Subject build() throws IncorrectAttributesException {
      if (name == null || affiliation == null) {
        throw new IncorrectAttributesException("Both name and affiliation attributes must be provided to builder.");
      }
      SubjectIdentifier id = new SubjectIdentifier(name, affiliation);
      Equipment equipment = new Equipment(weapon, shield, armor);
      healthPoints = getHealthPoints();
      if (race != null) {
        abilities = abilities.of(race.getAbilitiesModifiers());
        healthPoints += race.getAdditionalHealthPoints();
        CharacterClass cantripForClass = race.getCantripForClass();
        if (cantripForClass != null) {
          if (spells == null) {
            spells = new LinkedList<>();
          }
          Spell.of(cantripForClass, 0)
              .ifPresent(spells::add);
        }
      }
      return new Subject(id, race, characterClass, healthPoints, equipment, abilities, spells);
    }

    private int getHealthPoints() {
      if (healthPoints != 0) {
        return healthPoints;
      }
      return characterClass.getDefaultHealthPoints() + abilities.getConstitutionModifier();
    }
  }
}
