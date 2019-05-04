package dbryla.game.yetanotherengine.domain.subject;

import dbryla.game.yetanotherengine.domain.effects.Effect;
import dbryla.game.yetanotherengine.domain.operations.DamageType;
import dbryla.game.yetanotherengine.domain.subject.equipment.ArmorType;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import lombok.Getter;
import org.apache.commons.text.WordUtils;

import java.util.List;
import java.util.Set;

import static dbryla.game.yetanotherengine.domain.effects.Effect.LUCKY;
import static dbryla.game.yetanotherengine.domain.effects.Effect.RELENTLESS_ENDURANCE;
import static dbryla.game.yetanotherengine.domain.operations.DamageType.POISON;
import static dbryla.game.yetanotherengine.domain.subject.CharacterClass.WIZARD;
import static dbryla.game.yetanotherengine.domain.subject.equipment.ArmorType.LIGHT;
import static dbryla.game.yetanotherengine.domain.subject.equipment.ArmorType.MEDIUM;
import static dbryla.game.yetanotherengine.domain.subject.equipment.Weapon.*;

@Getter
public enum Race {
  HILL_DWARF(
      "Dwarf", List.of(0, 0, 2, 0, 1, 0),
      Set.of(HANDAXE, BATTLEAXE, HAMMER, WARHAMMER), Set.of(),
      1, null, Set.of(), Set.of(POISON), Set.of(POISON)),
  MOUNTAIN_DWARF(
      "Dwarf", List.of(2, 0, 2, 0, 0, 0),
      Set.of(HANDAXE, BATTLEAXE, HAMMER, WARHAMMER), Set.of(LIGHT, MEDIUM),
      0, null, Set.of(), Set.of(POISON), Set.of(POISON)),
  HIGH_ELF(
      "Elf", List.of(0, 2, 0, 1, 0, 0),
      Set.of(LONGSWORD, SHORTSWORD, SHORTBOW, LONGBOW), Set.of(),
      0, WIZARD, Set.of(), Set.of(), Set.of()),
  WOOD_ELF(
      "Elf", List.of(0, 2, 0, 0, 1, 0),
      Set.of(LONGSWORD, SHORTSWORD, SHORTBOW, LONGBOW), Set.of(),
      0, null, Set.of(), Set.of(), Set.of()),
  DARK_ELF(
      "Elf", List.of(0, 2, 0, 0, 0, 1),
      Set.of(RAPIER, SHORTSWORD, CROSSBOW), Set.of(),
      0, null, Set.of(), Set.of(), Set.of()),
  LIGHTFOOT_HALFLING(
      "Halfling", List.of(0, 2, 0, 0, 0, 1),
      Set.of(), Set.of(),
      0, null, Set.of(LUCKY), Set.of(), Set.of()),
  STOUT_HALFLING(
      "Halfling", List.of(0, 2, 1, 0, 0, 0),
      Set.of(), Set.of(),
      0, null, Set.of(LUCKY), Set.of(), Set.of()),
  HUMAN(
      "Human", List.of(1, 1, 1, 1, 1, 1),
      Set.of(), Set.of(),
      0, null, Set.of(), Set.of(), Set.of()),
  FOREST_GNOME(
      "Gnome", List.of(0, 1, 0, 2, 0, 0),
      Set.of(), Set.of(),
      0, null, Set.of(), Set.of(), Set.of()),
  ROCK_GNOME(
      "Gnome", List.of(0, 0, 1, 2, 0, 0),
      Set.of(), Set.of(),
      0, null, Set.of(), Set.of(), Set.of()),
  HALF_ELF(
      "Half-Elf", List.of(0, 1, 0, 1, 0, 2),
      Set.of(), Set.of(),
      0, null, Set.of(), Set.of(), Set.of()),
  HALF_ORC(
      "Half-Orc", List.of(2, 0, 1, 0, 0, 0),
      Set.of(), Set.of(),
      0, null, Set.of(RELENTLESS_ENDURANCE), Set.of(), Set.of()),
  TIEFLING(
      "Thiefling", List.of(0, 0, 0, 1, 0, 2),
      Set.of(), Set.of(),
      0, null, Set.of(), Set.of(), Set.of()),
  HUMANOID,
  GOBLINOID,
  BEAST,
  UNDEAD(Set.of(POISON));

  private final String displayName;
  private final List<Integer> abilitiesModifiers;
  private final Set<Weapon> weaponProficiencies;
  private final Set<ArmorType> armorProficiencies;
  private final int additionalHealthPoints;
  private final CharacterClass cantripForClass;
  private final Set<Effect> raceEffects;
  private final Set<DamageType> resistances;
  private final Set<DamageType> advantageOnSavingThrows;
  private final Set<DamageType> immunities;
  private final boolean playable;

  Race(String displayName, List<Integer> abilitiesModifiers, Set<Weapon> weaponProficiencies, Set<ArmorType> armorProficiencies,
       int additionalHealthPoints, CharacterClass cantripForClass, Set<Effect> raceEffects, Set<DamageType> resistances,
       Set<DamageType> advantageOnSavingThrows) {
    this.displayName = displayName;
    this.abilitiesModifiers = abilitiesModifiers;
    this.weaponProficiencies = weaponProficiencies;
    this.armorProficiencies = armorProficiencies;
    this.additionalHealthPoints = additionalHealthPoints;
    this.cantripForClass = cantripForClass;
    this.raceEffects = raceEffects;
    this.resistances = resistances;
    this.advantageOnSavingThrows = advantageOnSavingThrows;
    this.immunities = Set.of();
    this.playable = true;
  }

  Race() {
    this.displayName = null;
    this.abilitiesModifiers = List.of(0, 0, 0, 0, 0, 0);
    this.weaponProficiencies = Set.of();
    this.armorProficiencies = Set.of();
    this.additionalHealthPoints = 0;
    this.cantripForClass = null;
    this.raceEffects = Set.of();
    this.immunities = Set.of();
    this.resistances = Set.of();
    this.advantageOnSavingThrows = Set.of();
    this.playable = false;
  }

  Race(Set<DamageType> immunities) {
    this.displayName = null;
    this.abilitiesModifiers = List.of(0, 0, 0, 0, 0, 0);
    this.weaponProficiencies = Set.of();
    this.armorProficiencies = Set.of();
    this.additionalHealthPoints = 0;
    this.cantripForClass = null;
    this.raceEffects = Set.of();
    this.immunities = immunities;
    this.resistances = Set.of();
    this.advantageOnSavingThrows = Set.of();
    this.playable = false;
  }

  @Override
  public String toString() {
    return WordUtils.capitalizeFully(super.toString().replace("_", " "));
  }
}
