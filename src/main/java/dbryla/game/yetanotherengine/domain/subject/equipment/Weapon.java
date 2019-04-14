package dbryla.game.yetanotherengine.domain.subject.equipment;

import static dbryla.game.yetanotherengine.domain.subject.equipment.WeaponProperties.*;
import static dbryla.game.yetanotherengine.domain.subject.equipment.WeaponType.*;

import dbryla.game.yetanotherengine.domain.dice.DiceRollService;
import java.util.Set;
import java.util.stream.IntStream;

public enum Weapon {

  CLUB(SIMPLE_MELEE_WEAPON, 1, 4, SMASH_HEAD, LIGHT),
  DAGGER(SIMPLE_MELEE_WEAPON, 1, 4, CUT_THROAT, FINESSE, LIGHT),
  QUARTERSTAFF(SIMPLE_MELEE_WEAPON, 1, 6, SMASH_HEAD),
  HANDAXE(SIMPLE_MELEE_WEAPON, 1, 6, CHOP_HEAD, LIGHT),
  HAMMER(SIMPLE_MELEE_WEAPON, 1, 4, SMASH_HEAD, LIGHT),
  SHORTBOW(SIMPLE_RANGED_WEAPON, 1, 6, HEADSHOT, TWO_HANDED),
  GREATSWORD(MARTIAL_MELEE_WEAPON, 2, 6, CHOP_HEAD, TWO_HANDED),
  SHORTSWORD(MARTIAL_MELEE_WEAPON, 1, 6, CHOP_HEAD, FINESSE, LIGHT),
  SCIMITAR(MARTIAL_MELEE_WEAPON, 1, 6, CHOP_HEAD, FINESSE, LIGHT),
  BATTLEAXE(MARTIAL_MELEE_WEAPON, 1, 8, CHOP_HEAD),
  WARHAMMER(MARTIAL_MELEE_WEAPON, 1, 8, SMASH_HEAD),
  LONGSWORD(MARTIAL_MELEE_WEAPON, 1, 8, CHOP_HEAD),
  RAPIER(MARTIAL_MELEE_WEAPON, 1, 8, CUT_THROAT, FINESSE),
  LONGBOW(MARTIAL_RANGED_WEAPON, 1, 8, HEADSHOT, TWO_HANDED),
  CROSSBOW(MARTIAL_RANGED_WEAPON, 1, 6, HEADSHOT, LIGHT),
  BITE(MONSTER_MELEE_WEAPON, 1, 6, BITE_NECK);

  private final WeaponType type;
  private final int numberOfHitDice;
  private final int hitDice;
  private final String criticalHitMessage;
  private final Set<WeaponProperties> properties;

  Weapon(WeaponType type, int numberOfHitDice, int hitDice, String criticalHitMessage, WeaponProperties... properties) {
    this.type = type;
    this.numberOfHitDice = numberOfHitDice;
    this.hitDice = hitDice;
    this.criticalHitMessage = criticalHitMessage;
    this.properties = Set.of(properties);
  }

  public int rollAttackDamage(DiceRollService diceRollService) {
    return IntStream.range(0, numberOfHitDice).map(i -> diceRollService.of(hitDice)).sum();
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

  @Override
  public String toString() {
    return super.toString().toLowerCase();
  }
}
