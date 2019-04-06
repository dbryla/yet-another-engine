package dbryla.game.yetanotherengine.domain;

import dbryla.game.yetanotherengine.domain.subjects.classes.BaseClass;
import dbryla.game.yetanotherengine.domain.subjects.classes.Fighter;
import dbryla.game.yetanotherengine.domain.subjects.classes.Wizard;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Weapon;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GameOptionsTest {

  private GameOptions gameOptions = new GameOptions();

  @Test
  void shouldReturnAvailableClasses() {
    Set<Class> availableClasses = gameOptions.getAvailableClasses();

    assertThat(availableClasses).contains(Fighter.class, Wizard.class);
  }

  @Test
  void shouldReturnAvailableWeaponsForFighter() {
    Set<Weapon> availableWeapons = gameOptions.getAvailableWeapons(Fighter.class);

    assertThat(availableWeapons).contains(Weapon.values());
  }

  @Test
  void shouldReturnAvailableWeaponsForMage() {
    Set<Weapon> availableWeapons = gameOptions.getAvailableWeapons(Wizard.class);

    assertThat(availableWeapons).contains(Weapon.DAGGER, Weapon.QUARTERSTAFF);
  }

  @Test
  void shouldReturnAvailableArmorsForFighter() {
    Set<Armor> availableArmors = gameOptions.getAvailableArmors(Fighter.class);

    assertThat(availableArmors).contains(Armor.LEATHER, Armor.CHAIN_MAIL, Armor.CHAIN_SHIRT);
    assertThat(availableArmors).doesNotContain(Armor.SHIELD);
  }

  @Test
  void shouldReturnAvailableArmorsForMage() {
    Set<Armor> availableArmors = gameOptions.getAvailableArmors(Wizard.class);

    assertThat(availableArmors).isEmpty();
  }
}