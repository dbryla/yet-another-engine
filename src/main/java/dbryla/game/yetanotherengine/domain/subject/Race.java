package dbryla.game.yetanotherengine.domain.subject;

import dbryla.game.yetanotherengine.domain.effects.Effect;
import dbryla.game.yetanotherengine.domain.operations.DamageType;
import dbryla.game.yetanotherengine.domain.subject.equipment.ArmorType;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import lombok.Getter;
import org.apache.commons.text.WordUtils;

import java.util.List;
import java.util.Set;

import static dbryla.game.yetanotherengine.domain.effects.Effect.*;
import static dbryla.game.yetanotherengine.domain.effects.Effect.SLEEP;
import static dbryla.game.yetanotherengine.domain.operations.DamageType.*;
import static dbryla.game.yetanotherengine.domain.subject.equipment.Weapon.*;

@Getter
public enum Race {
  HILL_DWARF(
      "Dwarf", List.of(0, 0, 2, 0, 1, 0),
      Set.of(HANDAXE, BATTLEAXE, HAMMER, WARHAMMER), Set.of(), BuildingRaceTrait.ADDITIONAL_HEALTH_POINT,
      Set.of(), Set.of(POISON), Set.of(POISON), Set.of()),
  MOUNTAIN_DWARF(
      "Dwarf", List.of(2, 0, 2, 0, 0, 0),
      Set.of(HANDAXE, BATTLEAXE, HAMMER, WARHAMMER), Set.of(ArmorType.LIGHT, ArmorType.MEDIUM), null,
      Set.of(), Set.of(POISON), Set.of(POISON), Set.of()),
  HIGH_ELF(
      "Elf", List.of(0, 2, 0, 1, 0, 0),
      Set.of(LONGSWORD, SHORTSWORD, SHORTBOW, LONGBOW), Set.of(), BuildingRaceTrait.WIZARD_CANTRIP,
      Set.of(), Set.of(), Set.of(CHARMED), Set.of(SLEEP)),
  WOOD_ELF(
      "Elf", List.of(0, 2, 0, 0, 1, 0),
      Set.of(LONGSWORD, SHORTSWORD, SHORTBOW, LONGBOW), Set.of(), null,
      Set.of(), Set.of(), Set.of(CHARMED), Set.of(SLEEP)),
  DARK_ELF(
      "Elf", List.of(0, 2, 0, 0, 0, 1),
      Set.of(RAPIER, SHORTSWORD, CROSSBOW), Set.of(), null,
      Set.of(), Set.of(), Set.of(CHARMED), Set.of(SLEEP)),
  LIGHTFOOT_HALFLING(
      "Halfling", List.of(0, 2, 0, 0, 0, 1),
      Set.of(), Set.of(), null,
      Set.of(LUCKY), Set.of(), Set.of(FRIGHTENED), Set.of()),
  STOUT_HALFLING(
      "Halfling", List.of(0, 2, 1, 0, 0, 0),
      Set.of(), Set.of(), null,
      Set.of(LUCKY), Set.of(POISON), Set.of(FRIGHTENED, POISON), Set.of()),
  HUMAN(
      "Human", List.of(1, 1, 1, 1, 1, 1),
      Set.of(), Set.of(), null,
      Set.of(), Set.of(), Set.of(), Set.of()),
  FOREST_GNOME(
      "Gnome", List.of(0, 1, 0, 2, 0, 0),
      Set.of(), Set.of(), null,
      Set.of(), Set.of(), Set.of(), Set.of()),
  ROCK_GNOME(
      "Gnome", List.of(0, 0, 1, 2, 0, 0),
      Set.of(), Set.of(), null,
      Set.of(), Set.of(), Set.of(), Set.of()),
  HALF_ELF(
      "Half-Elf", List.of(0, 0, 0, 0, 0, 2),
      Set.of(), Set.of(), BuildingRaceTrait.TWO_ADDITIONAL_ABILITY_POINTS,
      Set.of(), Set.of(), Set.of(CHARMED), Set.of(SLEEP)),
  HALF_ORC(
      "Half-Orc", List.of(2, 0, 1, 0, 0, 0),
      Set.of(), Set.of(), null,
      Set.of(RELENTLESS_ENDURANCE, SAVAGE_ATTACK), Set.of(), Set.of(), Set.of()),
  TIEFLING(
      "Thiefling", List.of(0, 0, 0, 1, 0, 2),
      Set.of(), Set.of(), null,
      Set.of(), Set.of(FIRE), Set.of(), Set.of()),
  HUMANOID,
  GOBLINOID,
  BEAST,
  UNDEAD(Set.of(POISON, POISONED, EXHAUSTION), Set.of(BLUDGEONING));

  private final String displayName;
  private final List<Integer> abilitiesModifiers;
  private final Set<Weapon> weaponProficiencies;
  private final Set<ArmorType> armorProficiencies;
  private final BuildingRaceTrait buildingRaceTrait;
  private final Set<Effect> raceEffects;
  private final Set<DamageType> resistances;
  private final Set<DamageType> vulnerabilities;
  private final Set<? extends Enum> advantageOnSavingThrows;
  private final Set<? extends Enum> immunities;
  private final boolean playable;

  Race(String displayName, List<Integer> abilitiesModifiers, Set<Weapon> weaponProficiencies, Set<ArmorType> armorProficiencies,
       BuildingRaceTrait buildingRaceTrait, Set<Effect> raceEffects, Set<DamageType> resistances,
       Set<? extends Enum> advantageOnSavingThrows, Set<? extends Enum> immunities) {
    this.displayName = displayName;
    this.abilitiesModifiers = abilitiesModifiers;
    this.weaponProficiencies = weaponProficiencies;
    this.armorProficiencies = armorProficiencies;
    this.buildingRaceTrait = buildingRaceTrait;
    this.raceEffects = raceEffects;
    this.resistances = resistances;
    this.vulnerabilities = Set.of();
    this.advantageOnSavingThrows = advantageOnSavingThrows;
    this.immunities = immunities;
    this.playable = true;
  }

  Race() {
    this.displayName = null;
    this.abilitiesModifiers = List.of(0, 0, 0, 0, 0, 0);
    this.weaponProficiencies = Set.of();
    this.armorProficiencies = Set.of();
    this.buildingRaceTrait = null;
    this.raceEffects = Set.of();
    this.immunities = Set.of();
    this.resistances = Set.of();
    this.vulnerabilities = Set.of();
    this.advantageOnSavingThrows = Set.of();
    this.playable = false;
  }

  Race(Set<? extends Enum> immunities, Set<DamageType> vulnerabilities) {
    this.displayName = null;
    this.abilitiesModifiers = List.of(0, 0, 0, 0, 0, 0);
    this.weaponProficiencies = Set.of();
    this.armorProficiencies = Set.of();
    this.buildingRaceTrait = null;
    this.raceEffects = Set.of();
    this.immunities = immunities;
    this.resistances = Set.of();
    this.advantageOnSavingThrows = Set.of();
    this.vulnerabilities = vulnerabilities;
    this.playable = false;
  }

  @Override
  public String toString() {
    return WordUtils.capitalizeFully(super.toString().replace("_", " "));
  }

}
