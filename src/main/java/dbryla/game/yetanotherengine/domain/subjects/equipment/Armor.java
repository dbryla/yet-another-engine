package dbryla.game.yetanotherengine.domain.subjects.equipment;

import java.util.Optional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Armor {
  SHIELD(2, ArmorType.SHIELD),
  LEATHER(1, ArmorType.LIGHT),
  CHAIN_SHIRT(3, ArmorType.MEDIUM),
  CHAIN_MAIL(6, ArmorType.HEAVY),
  SCRAPS(1, ArmorType.MONSTER);

  private final int armorClass;
  private final ArmorType type;

  public int getArmorClass() {
    return armorClass;
  }

  public boolean isLightOrMedium() {
    return ArmorType.LIGHT.equals(type) || ArmorType.MEDIUM.equals(type);
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

  public boolean isPlayable() {
    return ArmorType.LIGHT.equals(type) || ArmorType.MEDIUM.equals(type) || ArmorType.HEAVY.equals(type);
  }

  @Override
  public String toString() {
    return super.toString().toLowerCase().replace("_", " ");
  }
}
