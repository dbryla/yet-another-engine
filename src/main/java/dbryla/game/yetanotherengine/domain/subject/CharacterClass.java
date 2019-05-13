package dbryla.game.yetanotherengine.domain.subject;

import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.equipment.Armor;
import dbryla.game.yetanotherengine.domain.equipment.Weapon;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.text.WordUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dbryla.game.yetanotherengine.domain.equipment.ArmorType.LIGHT;

@AllArgsConstructor
@Getter
public enum CharacterClass {

  FIGHTER(10, false, Position.PLAYERS_FRONT,
      Arrays.stream(Weapon.values()).filter(Weapon::isPlayable).collect(Collectors.toSet()),
      Arrays.stream(Armor.values()).filter(Armor::isPlayable).collect(Collectors.toSet())),
  WIZARD(6, true, Position.PLAYERS_BACK,
      Set.of(Weapon.DAGGER, Weapon.QUARTERSTAFF),
      Set.of()),
  CLERIC(8, true, Position.PLAYERS_BACK,
      Arrays.stream(Weapon.values()).filter(Weapon::isSimpleType).collect(Collectors.toSet()),
      Arrays.stream(Armor.values()).filter(Armor::isNotHeavyArmor).collect(Collectors.toSet())),
  ROGUE(8, false, Position.PLAYERS_FRONT,
      Stream.of(
          Arrays.stream(Weapon.values()).filter(Weapon::isSimpleType).collect(Collectors.toSet()),
          Set.of(Weapon.CROSSBOW, Weapon.LONGSWORD, Weapon.RAPIER, Weapon.SHORTSWORD))
          .flatMap(Collection::stream)
          .collect(Collectors.toSet()),
      Arrays.stream(Armor.values()).filter(armor -> LIGHT.equals(armor.getType())).collect(Collectors.toSet()));

  private final int defaultHealthPoints;
  private final boolean spellCaster;
  private final Position preferredPosition;
  private final Set<Weapon> weaponProficiencies;
  private final Set<Armor> armorProficiencies;

  @Override
  public String toString() {
    return WordUtils.capitalizeFully(super.toString());
  }

}
