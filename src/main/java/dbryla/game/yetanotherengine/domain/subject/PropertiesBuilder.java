package dbryla.game.yetanotherengine.domain.subject;

import dbryla.game.yetanotherengine.domain.encounters.SpecialAttack;
import dbryla.game.yetanotherengine.domain.equipment.Armor;
import dbryla.game.yetanotherengine.domain.equipment.Equipment;
import dbryla.game.yetanotherengine.domain.equipment.Weapon;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class PropertiesBuilder {

  private String name;
  private Affiliation affiliation;
  private CharacterClass characterClass;
  private Race race;
  private Abilities abilities;
  private List<Weapon> weapons = new LinkedList<>();
  private Armor shield;
  private Armor armor;
  private List<Spell> spells = List.of();
  private int healthPoints;
  private Set<SpecialAttack> specialAttacks = new HashSet<>();
  private int additionalHealthPoints;
  private Set<? extends Enum> advantageOnSavingThrows = new HashSet<>();

  public PropertiesBuilder name(String name) {
    this.name = name;
    return this;
  }

  public PropertiesBuilder affiliation(Affiliation affiliation) {
    this.affiliation = affiliation;
    return this;
  }

  public PropertiesBuilder characterClass(CharacterClass characterClass) {
    this.characterClass = characterClass;
    return this;
  }

  public PropertiesBuilder race(Race race) {
    this.race = race;
    return this;
  }

  public PropertiesBuilder weapons(List<Weapon> weapons) {
    this.weapons = weapons;
    return this;
  }

  public PropertiesBuilder weapon(Weapon weapon) {
    this.weapons.add(weapon);
    return this;
  }

  public PropertiesBuilder shield(Armor shield) {
    this.shield = shield;
    return this;
  }

  public PropertiesBuilder armor(Armor armor) {
    this.armor = armor;
    return this;
  }

  public PropertiesBuilder abilities(Abilities abilities) {
    this.abilities = abilities;
    return this;
  }

  public PropertiesBuilder spells(List<Spell> spells) {
    this.spells = spells;
    return this;
  }

  public PropertiesBuilder specialAttacks(Set<SpecialAttack> specialAttacks) {
    this.specialAttacks = specialAttacks;
    return this;
  }

  public PropertiesBuilder additionalHealthPoints(int additionalHealthPoints) {
    this.additionalHealthPoints = additionalHealthPoints;
    return this;
  }

  public PropertiesBuilder advantageOnSavingThrows(Set<? extends Enum> advantageOnSavingThrows) {
    this.advantageOnSavingThrows = advantageOnSavingThrows;
    return this;
  }

  /**
   * Replaces default class health points
   */
  public PropertiesBuilder healthPoints(int healthPoints) {
    this.healthPoints = healthPoints;
    return this;
  }

  public SubjectProperties build() {
    if (name == null || affiliation == null) {
      throw new IncorrectAttributesException("Both name and affiliation attributes must be provided to builder.");
    }
    Equipment equipment = new Equipment(weapons, shield, armor);
    abilities = abilities.of(race.getAbilitiesModifiers());
    healthPoints = getHealthPoints();
    return new SubjectProperties(name, affiliation, race, characterClass, equipment,
        abilities, spells, healthPoints, specialAttacks, advantageOnSavingThrows);
  }

  private int getHealthPoints() {
    if (healthPoints != 0) {
      return healthPoints;
    }
    return characterClass.getDefaultHealthPoints() + abilities.getConstitutionModifier() + additionalHealthPoints;
  }
}
