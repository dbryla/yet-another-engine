package dbryla.game.yetanotherengine.domain.effects;

import dbryla.game.yetanotherengine.domain.dice.HitDiceRollModifier;

public interface EffectLogic {
  int CONCENTRATION = -1;
  int FOREVER = -2;

  HitDiceRollModifier getSourceHitRollModifier();

  HitDiceRollModifier getTargetHitRollModifier();

  HitDiceRollModifier getTargetSavingThrowModifier();
}
