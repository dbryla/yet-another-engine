package dbryla.game.yetanotherengine.domain.subject.equipment;

import org.junit.jupiter.api.Test;

import static dbryla.game.yetanotherengine.domain.subject.equipment.Equipment.DEFAULT_ARMOR_CLASS;
import static org.assertj.core.api.Assertions.assertThat;

class EquipmentTest {

  @Test
  void shouldReturnArmorClassBasedOnEquipment() {
    Equipment equipment = new Equipment(null, Armor.SHIELD, Armor.CHAIN_MAIL);

    int armorClass = equipment.getArmorClass();

    assertThat(armorClass)
        .isEqualTo(DEFAULT_ARMOR_CLASS + Armor.SHIELD.getArmorClass() + Armor.CHAIN_MAIL.getArmorClass());
  }
}