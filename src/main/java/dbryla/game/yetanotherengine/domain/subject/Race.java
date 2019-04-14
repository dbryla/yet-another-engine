package dbryla.game.yetanotherengine.domain.subject;

import dbryla.game.yetanotherengine.domain.effects.Effect;
import dbryla.game.yetanotherengine.domain.subject.equipment.ArmorType;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Set;

import static dbryla.game.yetanotherengine.domain.effects.Effect.LUCKY;
import static dbryla.game.yetanotherengine.domain.effects.Effect.RELENTLESS_ENDURANCE;
import static dbryla.game.yetanotherengine.domain.subject.CharacterClass.WIZARD;
import static dbryla.game.yetanotherengine.domain.subject.equipment.ArmorType.LIGHT;
import static dbryla.game.yetanotherengine.domain.subject.equipment.ArmorType.MEDIUM;
import static dbryla.game.yetanotherengine.domain.subject.equipment.Weapon.*;

@Getter
@AllArgsConstructor
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
      0, null, Set.of());

  private final List<Integer> abilitiesModifiers;
  private final Set<Weapon> weaponProficiencies;
  private final Set<ArmorType> armorProficiencies;
  private final int additionalHealthPoints;
  private final CharacterClass cantripForClass;
  private final Set<Effect> classEffects;

  @Override
  public String toString() {
    return super.toString().toLowerCase().replace("_", " ");
  }
}
