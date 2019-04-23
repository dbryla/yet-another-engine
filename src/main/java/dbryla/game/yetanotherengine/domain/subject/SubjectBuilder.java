package dbryla.game.yetanotherengine.domain.subject;

import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.game.state.SubjectIdentifier;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subject.equipment.Equipment;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class SubjectBuilder {

  private String name;
  private String affiliation;
  private CharacterClass characterClass;
  private Race race;
  private Abilities abilities;
  private List<Weapon> weapons = new LinkedList<>();
  private Armor shield;
  private Armor armor;
  private List<Spell> spells;
  private int healthPoints;
  private Position position;
  private Weapon equippedWeapon = Weapon.FISTS;

  public SubjectBuilder name(String name) {
    this.name = name;
    return this;
  }

  public SubjectBuilder affiliation(String affiliation) {
    this.affiliation = affiliation;
    return this;
  }

  public SubjectBuilder characterClass(CharacterClass characterClass) {
    this.characterClass = characterClass;
    return this;
  }

  public SubjectBuilder race(Race race) {
    this.race = race;
    return this;
  }

  public SubjectBuilder weapons(List<Weapon> weapons) {
    this.weapons = weapons;
    return this;
  }

  public SubjectBuilder weapon(Weapon weapon) {
    this.weapons.add(weapon);
    return this;
  }

  public SubjectBuilder shield(Armor shield) {
    this.shield = shield;
    return this;
  }

  public SubjectBuilder armor(Armor armor) {
    this.armor = armor;
    return this;
  }

  public SubjectBuilder abilities(Abilities abilities) {
    this.abilities = abilities;
    return this;
  }

  public SubjectBuilder spells(List<Spell> spells) {
    this.spells = spells;
    return this;
  }

  public SubjectBuilder position(Position position) {
    this.position = position;
    return this;
  }

  public SubjectBuilder equippedWeapon(Weapon equippedWeapon) {
    this.equippedWeapon = equippedWeapon;
    return this;
  }

  /**
   * Replaces default class health points
   */
  public SubjectBuilder healthPoints(int healthPoints) {
    this.healthPoints = healthPoints;
    return this;
  }

  public Subject build() throws IncorrectAttributesException {
    if (name == null || affiliation == null) {
      throw new IncorrectAttributesException("Both name and affiliation attributes must be provided to builder.");
    }
    SubjectIdentifier id = new SubjectIdentifier(name, affiliation);
    Equipment equipment = new Equipment(weapons, shield, armor);
    abilities = abilities.of(race.getAbilitiesModifiers());
    healthPoints = getHealthPoints() + race.getAdditionalHealthPoints();
    CharacterClass cantripForClass = race.getCantripForClass();
    if (spells == null) {
      spells = new LinkedList<>();
    }
    if (cantripForClass != null) {
      Spell.of(cantripForClass, 0).ifPresent(spells::add);
    }
    SubjectProperties subjectProperties = new SubjectProperties(id, race, characterClass, equipment, abilities, spells, healthPoints);
    return new Subject(subjectProperties, healthPoints, position, new HashSet<>(), equippedWeapon);
  }

  private int getHealthPoints() {
    if (healthPoints != 0) {
      return healthPoints;
    }
    return characterClass.getDefaultHealthPoints() + abilities.getConstitutionModifier();
  }
}
