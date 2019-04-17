package dbryla.game.yetanotherengine.domain.subject.equipment;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Equipment {

  static final int DEFAULT_ARMOR_CLASS = 10;

  @Getter
  private final List<Weapon> weapons;
  private final Armor shield;
  private final Armor armor;

  public Equipment(Weapon weapon) {
    this.weapons = List.of(weapon);
    this.shield = null;
    this.armor = null;
  }

  public int getArmorClass(Weapon equippedWeapon) {
    int ac = DEFAULT_ARMOR_CLASS;
    if (shield != null && equippedWeapon.isEligibleForShield()) {
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

  public Optional<Armor> getShield() {
    return Optional.ofNullable(shield);
  }
}
