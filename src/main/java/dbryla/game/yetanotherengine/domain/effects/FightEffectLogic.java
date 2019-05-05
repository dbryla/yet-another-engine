package dbryla.game.yetanotherengine.domain.effects;

import dbryla.game.yetanotherengine.domain.Range;
import dbryla.game.yetanotherengine.domain.dice.HitDiceRollModifier;

public interface FightEffectLogic {
  int CONCENTRATION = -1;
  int FOREVER = -2;

  HitDiceRollModifier getSourceHitRollModifier();

  HitDiceRollModifier getTargetHitRollModifier(Range range);

  HitDiceRollModifier getTargetSavingThrowModifier();
}
