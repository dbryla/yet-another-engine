package dbryla.game.yetanotherengine.domain.equipment;

import dbryla.game.yetanotherengine.domain.Range;
import dbryla.game.yetanotherengine.domain.dice.DiceRollService;
import dbryla.game.yetanotherengine.domain.operations.DamageType;
import lombok.Getter;

import java.util.Set;
import java.util.stream.IntStream;

import static dbryla.game.yetanotherengine.domain.battleground.Distance.*;
import static dbryla.game.yetanotherengine.domain.equipment.WeaponProperties.*;
import static dbryla.game.yetanotherengine.domain.equipment.WeaponType.*;

public enum Weapon implements Range {

  CLUB(SIMPLE_MELEE_WEAPON, 1, 4, SMASH_HEAD, DamageType.BLUDGEONING, LIGHT),
  DAGGER(SIMPLE_MELEE_WEAPON, 1, 4, CUT_THROAT, DamageType.PIERCING, FINESSE, LIGHT),
  QUARTERSTAFF(SIMPLE_MELEE_WEAPON, 1, 6, SMASH_HEAD, DamageType.BLUDGEONING),
  HANDAXE(SIMPLE_MELEE_WEAPON, 1, 6, CHOP_HEAD, DamageType.SLASHING, LIGHT),
  HAMMER(SIMPLE_MELEE_WEAPON, 1, 4, SMASH_HEAD, DamageType.BLUDGEONING, LIGHT),
  SPEAR(SIMPLE_MELEE_WEAPON, 1, 6, STAB, DamageType.PIERCING),
  SHORTBOW(SIMPLE_RANGED_WEAPON, 1, 6, HEADSHOT, EIGHTY_FEET, DamageType.PIERCING, TWO_HANDED),
  GREATSWORD(MARTIAL_MELEE_WEAPON, 2, 6, CHOP_HEAD, DamageType.SLASHING, TWO_HANDED),
  SHORTSWORD(MARTIAL_MELEE_WEAPON, 1, 6, STAB, DamageType.PIERCING, FINESSE, LIGHT),
  SCIMITAR(MARTIAL_MELEE_WEAPON, 1, 6, CHOP_HEAD, DamageType.SLASHING, FINESSE, LIGHT),
  BATTLEAXE(MARTIAL_MELEE_WEAPON, 1, 8, CHOP_HEAD, DamageType.SLASHING),
  WARHAMMER(MARTIAL_MELEE_WEAPON, 1, 8, SMASH_HEAD, DamageType.BLUDGEONING),
  LONGSWORD(MARTIAL_MELEE_WEAPON, 1, 8, CHOP_HEAD, DamageType.SLASHING),
  RAPIER(MARTIAL_MELEE_WEAPON, 1, 8, STAB, DamageType.PIERCING, FINESSE),
  LONGBOW(MARTIAL_RANGED_WEAPON, 1, 8, HEADSHOT, ONE_HUNDRED_FIFTY_FEET, DamageType.PIERCING, TWO_HANDED),
  CROSSBOW(MARTIAL_RANGED_WEAPON, 1, 6, HEADSHOT, THIRTY_FEET, DamageType.PIERCING, LIGHT),
  BITE(MONSTER_MELEE_WEAPON, 1, 6, BITE_NECK, DamageType.PIERCING),
  CLAW(MONSTER_MELEE_WEAPON, 1, 4, CUT_THROAT, DamageType.SLASHING),
  FISTS(NON_PLAYABLE, 1, 1, SMASH_HEAD, DamageType.BLUDGEONING);

  private final WeaponType type;
  private final int numberOfHitDice;
  private final int hitDice;
  private final String criticalHitMessage;
  @Getter
  private final int maxRange;
  @Getter
  private final DamageType damageType;
  private final Set<WeaponProperties> properties;

  Weapon(WeaponType type, int numberOfHitDice, int hitDice, String criticalHitMessage,
         DamageType damageType, WeaponProperties... properties) {
    this.type = type;
    this.numberOfHitDice = numberOfHitDice;
    this.hitDice = hitDice;
    this.criticalHitMessage = criticalHitMessage;
    this.damageType = damageType;
    this.maxRange = CLOSE_RANGE;
    this.properties = Set.of(properties);
  }

  Weapon(WeaponType type, int numberOfHitDice, int hitDice, String criticalHitMessage, int maxRange,
         DamageType damageType, WeaponProperties... properties) {
    this.type = type;
    this.numberOfHitDice = numberOfHitDice;
    this.hitDice = hitDice;
    this.criticalHitMessage = criticalHitMessage;
    this.maxRange = maxRange;
    this.damageType = damageType;
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
    return MONSTER_MELEE_WEAPON.equals(type) || SIMPLE_MELEE_WEAPON.equals(type) || MARTIAL_MELEE_WEAPON.equals(type);
  }

  public boolean isRanged() {
    return SIMPLE_RANGED_WEAPON.equals(type) || MARTIAL_RANGED_WEAPON.equals(type);
  }

  public boolean isFinesse() {
    return properties.contains(FINESSE);
  }

  public String getCriticalHitMessage() {
    return criticalHitMessage + this.toString().toLowerCase() + ".";
  }

  public boolean isPlayable() {
    return !MONSTER_MELEE_WEAPON.equals(type) && !NON_PLAYABLE.equals(type);
  }

  @Override
  public String toString() {
    return super.toString().toLowerCase();
  }

  public int getMinRange() {
    return isMelee() ? CLOSE_RANGE : THIRTY_FEET;
  }

  @Override
  public boolean isClose() {
    return isMelee();
  }
}