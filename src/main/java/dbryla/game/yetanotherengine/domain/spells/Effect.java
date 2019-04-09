package dbryla.game.yetanotherengine.domain.spells;

import static dbryla.game.yetanotherengine.domain.spells.DiceRollModifier.*;

import dbryla.game.yetanotherengine.domain.subjects.ActiveEffect;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Effect {
  BLIND(DISADVANTAGE, ADVANTAGE, NONE, 1),
  BLESS(BLESSED, NONE, BLESSED, SpellConstants.CONCENTRATION);

  private final DiceRollModifier sourceHitRollModifier;
  private final DiceRollModifier targetHitRollModifier;
  private final DiceRollModifier targetSavingThrowModifier;
  private final int durationInTurns;

  public ActiveEffect activate() {
    return new ActiveEffect(this, durationInTurns);
  }
}
