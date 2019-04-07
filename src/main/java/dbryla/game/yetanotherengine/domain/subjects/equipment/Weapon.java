package dbryla.game.yetanotherengine.domain.subjects.equipment;

import dbryla.game.yetanotherengine.domain.DiceRoll;

import java.util.Set;
import java.util.function.Supplier;

import static dbryla.game.yetanotherengine.domain.subjects.equipment.WeaponProperties.*;
import static dbryla.game.yetanotherengine.domain.subjects.equipment.WeaponType.*;

public enum Weapon {

  CLUB(SIMPLE_MELEE_WEAPON, DiceRoll::k4, LIGHT),
  DAGGER(SIMPLE_MELEE_WEAPON, DiceRoll::k4, FINESSE, LIGHT),
  QUARTERSTAFF(SIMPLE_MELEE_WEAPON, DiceRoll::k6),
  GREATSWORD(MARTIAL_MELEE_WEAPON, () -> DiceRoll.k6() + DiceRoll.k6(), TWO_HANDED),
  SHORTSWORD(MARTIAL_MELEE_WEAPON, DiceRoll::k6, FINESSE, LIGHT),
  SHORTBOW(SIMPLE_RANGED_WEAPON, DiceRoll::k6, TWO_HANDED),
  LONGBOW(MARTIAL_RANGED_WEAPON, DiceRoll::k8, TWO_HANDED);

  private final WeaponType type;
  private final Supplier<Integer> diceRoll;
  private final Set<WeaponProperties> properties;

  Weapon(WeaponType type, Supplier<Integer> diceRoll, WeaponProperties... properties) {
    this.type = type;
    this.diceRoll = diceRoll;
    this.properties = Set.of(properties);
  }

  public int rollAttackDamage() {
    return diceRoll.get();
  }

  public boolean isEligibleForShield() {
    return !properties.contains(TWO_HANDED);
  }

  public boolean isSimpleType() {
    return SIMPLE_MELEE_WEAPON.equals(type) || SIMPLE_RANGED_WEAPON.equals(type);
  }

  public boolean isMelee() {
    return SIMPLE_MELEE_WEAPON.equals(type) || MARTIAL_MELEE_WEAPON.equals(type);
  }

  public boolean isFinesse() {
    return properties.contains(FINESSE);
  }
}
