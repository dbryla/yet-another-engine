package dbryla.game.yetanotherengine.domain.game;

import dbryla.game.yetanotherengine.domain.subject.CharacterClass;
import dbryla.game.yetanotherengine.domain.subject.Race;
import dbryla.game.yetanotherengine.domain.subject.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class GameOptions {

  public static final String PLAYERS = "player";
  public static final String ENEMIES = "enemies";

  public Set<CharacterClass> getAvailableClasses() {
    return Set.of(CharacterClass.values());
  }

  public Set<Weapon> getAvailableWeapons(CharacterClass characterClass, Race race) {
    Set<Weapon> weapons = new HashSet<>(characterClass.getWeaponProficiencies());
    weapons.addAll(race.getWeaponProficiencies());
    return weapons;
  }

  public Set<Armor> getAvailableArmors(CharacterClass characterClass, Race race) {
    Set<Armor> armors = new HashSet<>(characterClass.getArmorProficiencies());
    armors.remove(Armor.SHIELD);
    armors.addAll(
        Arrays.stream(Armor.values())
            .filter(armor -> race.getArmorProficiencies().contains(armor.getType()))
            .collect(Collectors.toSet()));
    return armors;
  }

  public Set<Race> getAvailableRaces() {
    return Arrays.stream(Race.values()).filter(Race::isPlayable).collect(Collectors.toSet());
  }
}
