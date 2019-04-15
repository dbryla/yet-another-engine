package dbryla.game.yetanotherengine.domain.subject;

import static dbryla.game.yetanotherengine.domain.effects.Effect.LUCKY;
import static dbryla.game.yetanotherengine.domain.effects.Effect.RELENTLESS_ENDURANCE;
import static dbryla.game.yetanotherengine.domain.subject.CharacterClass.WIZARD;
import static dbryla.game.yetanotherengine.domain.subject.equipment.ArmorType.LIGHT;
import static dbryla.game.yetanotherengine.domain.subject.equipment.ArmorType.MEDIUM;
import static dbryla.game.yetanotherengine.domain.subject.equipment.Weapon.BATTLEAXE;
import static dbryla.game.yetanotherengine.domain.subject.equipment.Weapon.CROSSBOW;
import static dbryla.game.yetanotherengine.domain.subject.equipment.Weapon.HAMMER;
import static dbryla.game.yetanotherengine.domain.subject.equipment.Weapon.HANDAXE;
import static dbryla.game.yetanotherengine.domain.subject.equipment.Weapon.LONGBOW;
import static dbryla.game.yetanotherengine.domain.subject.equipment.Weapon.LONGSWORD;
import static dbryla.game.yetanotherengine.domain.subject.equipment.Weapon.RAPIER;
import static dbryla.game.yetanotherengine.domain.subject.equipment.Weapon.SHORTBOW;
import static dbryla.game.yetanotherengine.domain.subject.equipment.Weapon.SHORTSWORD;
import static dbryla.game.yetanotherengine.domain.subject.equipment.Weapon.WARHAMMER;

import dbryla.game.yetanotherengine.domain.effects.Effect;
import dbryla.game.yetanotherengine.domain.subject.equipment.ArmorType;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import java.util.List;
import java.util.Set;
import lombok.Getter;

@Getter
public enum Race {
  HILL_DWARF(
      List.of(0, 0, 2, 0, 1, 0),
      Set.of(HANDAXE, BATTLEAXE, HAMMER, WARHAMMER), Set.of(),
      1, null, Set.of()),
  MOUNTAIN_DWARF(
      List.of(2, 0, 2, 0, 0, 0),
      Set.of(HANDAXE, BATTLEAXE, HAMMER, WARHAMMER), Set.of(LIGHT, MEDIUM),
      0, null, Set.of()),
  HIGH_ELF(
      List.of(0, 2, 0, 1, 0, 0),
      Set.of(LONGSWORD, SHORTSWORD, SHORTBOW, LONGBOW), Set.of(),
      0, WIZARD, Set.of()),
  WOOD_ELF(
      List.of(0, 2, 0, 0, 1, 0),
      Set.of(LONGSWORD, SHORTSWORD, SHORTBOW, LONGBOW), Set.of(),
      0, null, Set.of()),
  DARK_ELF(
      List.of(0, 2, 0, 0, 0, 1),
      Set.of(RAPIER, SHORTSWORD, CROSSBOW), Set.of(),
      0, null, Set.of()),
  LIGHTFOOT_HALFLING(
      List.of(0, 2, 0, 0, 0, 1),
      Set.of(), Set.of(),
      0, null, Set.of(LUCKY)),
  STOUT_HALFLING(
      List.of(0, 2, 1, 0, 0, 0),
      Set.of(), Set.of(),
      0, null, Set.of(LUCKY)),
  HUMAN(
      List.of(1, 1, 1, 1, 1, 1),
      Set.of(), Set.of(),
      0, null, Set.of()),
  FOREST_GNOME(
      List.of(0, 1, 0, 2, 0, 0),
      Set.of(), Set.of(),
      0, null, Set.of()),
  ROCK_GNOME(
      List.of(0, 0, 1, 2, 0, 0),
      Set.of(), Set.of(),
      0, null, Set.of()),
  HALF_ELF(
      List.of(0, 1, 0, 1, 0, 2),
      Set.of(), Set.of(),
      0, null, Set.of()),
  HALF_ORC(
      List.of(2, 0, 1, 0, 0, 0),
      Set.of(), Set.of(),
      0, null, Set.of(RELENTLESS_ENDURANCE)),
  TIEFLING(
      List.of(0, 0, 0, 1, 0, 2),
      Set.of(), Set.of(),
      0, null, Set.of()),
  HUMANOID,
  GOBLINOID,
  BEAST,
  UNDEAD;

  private final List<Integer> abilitiesModifiers;
  private final Set<Weapon> weaponProficiencies;
  private final Set<ArmorType> armorProficiencies;
  private final int additionalHealthPoints;
  private final CharacterClass cantripForClass;
  private final Set<Effect> classEffects;
  private final boolean playable;

  Race(List<Integer> abilitiesModifiers, Set<Weapon> weaponProficiencies,
      Set<ArmorType> armorProficiencies, int additionalHealthPoints, CharacterClass cantripForClass,
      Set<Effect> classEffects) {
    this.abilitiesModifiers = abilitiesModifiers;
    this.weaponProficiencies = weaponProficiencies;
    this.armorProficiencies = armorProficiencies;
    this.additionalHealthPoints = additionalHealthPoints;
    this.cantripForClass = cantripForClass;
    this.classEffects = classEffects;
    this.playable = true;
  }

  Race() {
    this.abilitiesModifiers = List.of(0, 0, 0, 0, 0, 0);
    this.weaponProficiencies = Set.of();
    this.armorProficiencies = Set.of();
    this.additionalHealthPoints = 0;
    this.cantripForClass = null;
    this.classEffects = Set.of();
    this.playable = false;
  }

  @Override
  public String toString() {
    return super.toString().toLowerCase().replace("_", " ");
  }
}
