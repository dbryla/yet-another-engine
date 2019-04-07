package dbryla.game.yetanotherengine.domain.subjects.equipment;

import lombok.AllArgsConstructor;

import java.util.Optional;

import static dbryla.game.yetanotherengine.domain.subjects.equipment.ArmorType.MEDIUM;

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
    return !ArmorType.HEAVY.equals(type) && !ArmorType.SHIELD.equals(type);
  }

  public Optional<Integer> getMaxDexterityBonus() {
    switch (type) {
      case MEDIUM:
        return Optional.of(2);
      case HEAVY:
        return Optional.of(0);
      default:
        return Optional.empty();
    }
  }
}
