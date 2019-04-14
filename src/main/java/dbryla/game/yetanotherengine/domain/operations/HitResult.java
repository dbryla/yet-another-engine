package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.equipment.Armor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum HitResult {
  CRITICAL(true, "critically"),
  AUTO_FAIL(false, "%s tries to hit %s with %s but fails spectacularly."),
  MISS(false, "%s misses attack on %s with %s."),
  ARMOR(false, "%s hits %s's armor with %s, but it's untouched."),
  SHIELD(false, "%s hits %s's shield with %s, but it's untouched."),
  DODGE(false, "%s attacks %s with %s, but they dodge it."),
  BARELY_HIT(true, "barely"),
  EASILY_HIT(true, "easily"),
  HIT(true, "");

  private final boolean isTargetHit;
  private final String message;

  public static HitResult of(HitRoll hitRoll, Subject target) {
    if (hitRoll.getOriginal() == 20) {
      return HitResult.CRITICAL;
    }
    if (hitRoll.getOriginal() == 1) {
      return HitResult.AUTO_FAIL;
    }
    if (miss(hitRoll, target)) {
      return handleMiss(hitRoll, target);
    } else {
      return handleHit(hitRoll, target);
    }
  }

  private static boolean miss(HitRoll hitRoll, Subject target) {
    return hitRoll.getActual() < target.getArmorClass();
  }

  private static HitResult handleMiss(HitRoll hitRoll, Subject target) {
    if (hitRoll.getActual() < 10 - negativeDexterityModifier(target)) {
      return HitResult.MISS;
    }
    if (target.getEquipment().getArmor().isPresent()
        && hitRoll.getActual() < 10 - negativeDexterityModifier(target) + target.getEquipment().getArmor().get().getArmorClass()) {
      return HitResult.ARMOR;
    }
    if (target.getEquipment().getShield().isPresent() &&
        hitRoll.getActual() < 10 - negativeDexterityModifier(target)
            + target.getEquipment().getArmor().map(Armor::getArmorClass).orElse(0)
            + target.getEquipment().getShield().get().getArmorClass()) {
      return HitResult.SHIELD;
    }
    return HitResult.DODGE;
  }

  private static HitResult handleHit(HitRoll hitRoll, Subject target) {
    if (hitRoll.getActual() == target.getArmorClass()) {
      return HitResult.BARELY_HIT;
    }
    if (hitRoll.getActual() > target.getArmorClass() + 5) {
      return HitResult.EASILY_HIT;
    }
    return HitResult.HIT;
  }

  private static int negativeDexterityModifier(Subject target) {
    if (target.getAbilities().getDexterityModifier() < 0) {
      return target.getAbilities().getDexterityModifier();
    }
    return 0;
  }
}
