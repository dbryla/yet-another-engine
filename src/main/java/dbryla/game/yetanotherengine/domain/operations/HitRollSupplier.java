package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.subjects.Subject;
import org.springframework.stereotype.Component;

@Component
public class HitRollSupplier {

  public int get(Subject source, Subject target) {
    if (source.getActiveEffect().isPresent()) {
      return source.getActiveEffect().get().getSourceModifier().getDiceRollModifier();
    }
    if (target.getActiveEffect().isPresent()) {
      return target.getActiveEffect().get().getTargetModifier().getDiceRollModifier();
    }
    return source.calculateWeaponHitRoll();
  }
}
