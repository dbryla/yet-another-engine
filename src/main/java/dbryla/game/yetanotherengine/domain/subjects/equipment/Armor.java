package dbryla.game.yetanotherengine.domain.subjects.equipment;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Armor {
  SHIELD(2, ArmorType.SHIELD),
  LEATHER(1, ArmorType.LIGHT),
  CHAIN_SHIRT(3, ArmorType.MEDIUM),
  CHAIN_MAIL(6, ArmorType.HEAVY);

  private final int armorClass;
  private final ArmorType type;

  public int getArmorClass() {
    return armorClass;
  }

  public boolean isNotHeavyArmor() {
    return !type.equals(ArmorType.HEAVY) && !type.equals(ArmorType.SHIELD);
  }
}
