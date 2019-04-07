package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.subjects.classes.Subject;

public enum HitFlavor {
  CRITICAL, TRUE_MISS, MISS;

  public static HitFlavor of(HitRoll hitRoll, Subject target) {
    if (hitRoll.getOriginal() == 20) {
      return HitFlavor.CRITICAL;
    }
    if (hitRoll.getOriginal() == 1) {
      return HitFlavor.TRUE_MISS;
    }
    if (hitRoll.getActual() < target.getArmorClass()) {
      if (hitRoll.getActual() < 10) {
        return HitFlavor.MISS;
      }
      return null;
    }
  }
}
