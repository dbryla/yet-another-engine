package dbryla.game.yetanotherengine.domain.subjects.equipment;

import static dbryla.game.yetanotherengine.domain.subjects.equipment.WeaponProperties.*;
import static dbryla.game.yetanotherengine.domain.subjects.equipment.WeaponType.*;

import dbryla.game.yetanotherengine.domain.DiceRoll;
import java.util.Set;
import java.util.function.Supplier;

public enum Weapon {

  CLUB(SIMPLE_MELEE_WEAPON, DiceRoll::k4, SMASH_HEAD, LIGHT),
  DAGGER(SIMPLE_MELEE_WEAPON, DiceRoll::k4, CUT_THROAT, FINESSE, LIGHT),
  QUARTERSTAFF(SIMPLE_MELEE_WEAPON, DiceRoll::k6, SMASH_HEAD),
  GREATSWORD(MARTIAL_MELEE_WEAPON, () -> DiceRoll.k6() + DiceRoll.k6(), CHOP_HEAD, TWO_HANDED),
  SHORTSWORD(MARTIAL_MELEE_WEAPON, DiceRoll::k6, CHOP_HEAD, FINESSE, LIGHT),
  SHORTBOW(SIMPLE_RANGED_WEAPON, DiceRoll::k6, HEADSHOT, TWO_HANDED),
  LONGBOW(MARTIAL_RANGED_WEAPON, DiceRoll::k8, HEADSHOT, TWO_HANDED),
  SCIMITAR(MARTIAL_MELEE_WEAPON, DiceRoll::k6, CHOP_HEAD, FINESSE, LIGHT),
  BITE(MONSTER_MELEE_WEAPON, DiceRoll::k6, BITE_NECK);


  private final WeaponType type;
  private final Supplier<Integer> diceRoll;
  private final String criticalHitMessage;
  private final Set<WeaponProperties> properties;

  Weapon(WeaponType type, Supplier<Integer> diceRoll, String criticalHitMessage, WeaponProperties... properties) {
    this.type = type;
    this.diceRoll = diceRoll;
    this.criticalHitMessage = criticalHitMessage;
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

  public String getCriticalHitMessage() {
    return criticalHitMessage + this.toString().toLowerCase() + ".";
  }

  public boolean isPlayable() {
    return !MONSTER_MELEE_WEAPON.equals(type);
  }
}