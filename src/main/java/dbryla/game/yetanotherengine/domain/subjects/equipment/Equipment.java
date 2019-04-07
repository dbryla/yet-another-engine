package dbryla.game.yetanotherengine.domain.subjects.equipment;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

import static dbryla.game.yetanotherengine.domain.subjects.equipment.ArmorType.MEDIUM;

@AllArgsConstructor
public class Equipment {

  static final int DEFAULT_ARMOR_CLASS = 10;

  @Getter
  private final Weapon weapon;
  private final Armor shield;
  private final Armor armor;

  public Equipment(Weapon weapon) {
    this.weapon = weapon;
    this.shield = null;
    this.armor = null;
  }

  public int getArmorClass() {
    int ac = DEFAULT_ARMOR_CLASS;
    if (shield != null) {
      ac += shield.getArmorClass();
    }
    if (armor != null) {
      ac += armor.getArmorClass();
    }
    return ac;
  }

  public Optional<Armor> getArmor() {
    return Optional.ofNullable(armor);
  }
}
