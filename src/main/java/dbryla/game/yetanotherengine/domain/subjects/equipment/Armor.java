package dbryla.game.yetanotherengine.domain.subjects.equipment;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Armor {
  SHIELD(2),
  LEATHER(1),
  CHAIN_SHIRT(3),
  CHAIN_MAIL(6);

  private final int armorClass;

  public int getArmorClass() {
    return armorClass;
  }
}
