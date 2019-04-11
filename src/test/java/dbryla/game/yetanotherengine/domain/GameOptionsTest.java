package dbryla.game.yetanotherengine.domain;

import static org.assertj.core.api.Assertions.assertThat;

import dbryla.game.yetanotherengine.domain.encounters.MonsterBook;
import dbryla.game.yetanotherengine.domain.subjects.classes.Fighter;
import dbryla.game.yetanotherengine.domain.subjects.classes.Wizard;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Weapon;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;

class GameOptionsTest {

  private GameOptions gameOptions = new GameOptions(new MonsterBook(new Random()));

  @Test
  void shouldReturnAvailableClasses() {
    Set<Class> availableClasses = gameOptions.getAvailableClasses();

    assertThat(availableClasses).contains(Fighter.class, Wizard.class);
  }

  @Test
  void shouldReturnAvailableWeaponsForFighter() {
    Set<Weapon> availableWeapons = gameOptions.getAvailableWeapons(Fighter.class.getSimpleName());

    assertThat(availableWeapons).contains(Weapon.SHORTSWORD, Weapon.SHORTBOW, Weapon.CLUB, Weapon.LONGBOW);
  }

  @Test
  void shouldReturnAvailableWeaponsForMage() {
    Set<Weapon> availableWeapons = gameOptions.getAvailableWeapons(Wizard.class.getSimpleName());

    assertThat(availableWeapons).contains(Weapon.DAGGER, Weapon.QUARTERSTAFF);
  }

  @Test
  void shouldReturnAvailableArmorsForFighter() {
    Set<Armor> availableArmors = gameOptions.getAvailableArmors(Fighter.class.getSimpleName());

    assertThat(availableArmors).contains(Armor.LEATHER, Armor.CHAIN_MAIL, Armor.CHAIN_SHIRT);
    assertThat(availableArmors).doesNotContain(Armor.SHIELD);
  }

  @Test
  void shouldReturnAvailableArmorsForMage() {
    Set<Armor> availableArmors = gameOptions.getAvailableArmors(Wizard.class.getSimpleName());

    assertThat(availableArmors).isEmpty();
  }
}