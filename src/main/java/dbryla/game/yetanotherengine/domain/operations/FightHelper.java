package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.DiceRoll;
import dbryla.game.yetanotherengine.domain.subjects.classes.Subject;
import org.springframework.stereotype.Component;

@Component
public class FightHelper {

  int getHitRoll(Subject source, Subject target) {
    if (source.getActiveEffect().isPresent()) {
      return source.getActiveEffect().get().getEffect().getSourceModifier().getDiceRollModifier();
    }
    if (target.getActiveEffect().isPresent()) {
      return target.getActiveEffect().get().getEffect().getTargetModifier().getDiceRollModifier();
    }
    return DiceRoll.k20();
  }

  public boolean isMiss(int armorClass, int hitRoll) {
    return hitRoll == 1 || hitRoll < armorClass;
  }

  public int getAttackDamage(int attackDamage, int hitRoll) {
    if (hitRoll == 20) {
      return attackDamage * 2;
    }
    return attackDamage;
  }
}
