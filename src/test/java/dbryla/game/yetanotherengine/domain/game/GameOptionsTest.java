package dbryla.game.yetanotherengine.domain.game;

import dbryla.game.yetanotherengine.domain.subject.CharacterClass;
import dbryla.game.yetanotherengine.domain.subject.Race;
import dbryla.game.yetanotherengine.domain.equipment.Armor;
import dbryla.game.yetanotherengine.domain.equipment.Weapon;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GameOptionsTest {

  private GameOptions gameOptions = new GameOptions();

  @Test
  void shouldReturnAvailableClasses() {
    Set<CharacterClass> availableClasses = gameOptions.getAvailableClasses();

    assertThat(availableClasses).contains(CharacterClass.FIGHTER, CharacterClass.CLERIC, CharacterClass.WIZARD);
  }

  @Test
  void shouldReturnAvailableWeaponsForFighter() {
    Set<Weapon> availableWeapons = gameOptions.getAvailableWeapons(CharacterClass.FIGHTER, Race.HALF_ELF);

    assertThat(availableWeapons).contains(Weapon.SHORTSWORD, Weapon.SHORTBOW, Weapon.CLUB, Weapon.LONGBOW);
  }

  @Test
  void shouldReturnAvailableWeaponsForWizardAndElf() {
    Set<Weapon> availableWeapons = gameOptions.getAvailableWeapons(CharacterClass.WIZARD, Race.HIGH_ELF);

    assertThat(availableWeapons).contains(Weapon.DAGGER, Weapon.QUARTERSTAFF, Weapon.SHORTSWORD);
  }

  @Test
  void shouldReturnAvailableArmorsForFighter() {
    Set<Armor> availableArmors = gameOptions.getAvailableArmors(CharacterClass.FIGHTER, Race.HALF_ELF);

    assertThat(availableArmors).contains(Armor.LEATHER, Armor.CHAIN_MAIL, Armor.CHAIN_SHIRT);
    assertThat(availableArmors).doesNotContain(Armor.SHIELD);
  }

  @Test
  void shouldReturnAvailableArmorsForWizardAndElf() {
    Set<Armor> availableArmors = gameOptions.getAvailableArmors(CharacterClass.WIZARD, Race.HIGH_ELF);

    assertThat(availableArmors).isEmpty();
  }

  @Test
  void shouldReturnAvailableArmorsForWizardAndDwarf() {
    Set<Armor> availableArmors = gameOptions.getAvailableArmors(CharacterClass.WIZARD, Race.MOUNTAIN_DWARF);

    assertThat(availableArmors).isNotEmpty();
    assertThat(availableArmors).contains(Armor.CHAIN_SHIRT);
  }
}