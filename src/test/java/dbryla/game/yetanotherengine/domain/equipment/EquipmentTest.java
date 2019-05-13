package dbryla.game.yetanotherengine.domain.equipment;

import static dbryla.game.yetanotherengine.domain.equipment.Equipment.DEFAULT_ARMOR_CLASS;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EquipmentTest {

  @Test
  void shouldReturnArmorClassBasedOnEquipmentAndEquippedWeapon() {
    Equipment equipment = new Equipment(null, Armor.SHIELD, Armor.CHAIN_MAIL);

    int armorClass = equipment.getArmorClass(Weapon.SHORTSWORD);

    assertThat(armorClass)
        .isEqualTo(DEFAULT_ARMOR_CLASS + Armor.SHIELD.getArmorClass() + Armor.CHAIN_MAIL.getArmorClass());
  }

  @Test
  void shouldNotAddShieldArmorClassWhenUsingTwoHandWeapon() {
    Equipment equipment = new Equipment(null, Armor.SHIELD, Armor.CHAIN_MAIL);

    int armorClass = equipment.getArmorClass(Weapon.LONGBOW);

    assertThat(armorClass)
        .isEqualTo(DEFAULT_ARMOR_CLASS + Armor.CHAIN_MAIL.getArmorClass());
  }
}