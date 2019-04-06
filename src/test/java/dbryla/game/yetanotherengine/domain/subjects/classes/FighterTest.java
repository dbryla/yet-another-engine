package dbryla.game.yetanotherengine.domain.subjects.classes;

import dbryla.game.yetanotherengine.domain.subjects.equipment.Armor;
import org.junit.jupiter.api.Test;

import static dbryla.game.yetanotherengine.domain.subjects.classes.BaseClass.DEFAULT_ARMOR_CLASS;
import static org.assertj.core.api.Assertions.assertThat;

class FighterTest {

  @Test
  void shouldReturnArmorClassBasedOnEquipment() {
    Fighter fighter = Fighter.builder()
        .name("fighter")
        .affiliation("blue")
        .shield(Armor.SHIELD)
        .armor(Armor.CHAIN_MAIL)
        .build();

    int armorClass = fighter.getArmorClass();

    assertThat(armorClass)
        .isEqualTo(DEFAULT_ARMOR_CLASS + Armor.SHIELD.getArmorClass() + Armor.CHAIN_MAIL.getArmorClass());
  }
}