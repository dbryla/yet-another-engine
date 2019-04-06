package dbryla.game.yetanotherengine.domain.spells;

import static dbryla.game.yetanotherengine.domain.DiceRollModifier.ADVANTAGE;
import static dbryla.game.yetanotherengine.domain.DiceRollModifier.DISADVANTAGE;

import dbryla.game.yetanotherengine.domain.DiceRollModifier;
import dbryla.game.yetanotherengine.domain.subjects.classes.ActiveEffect;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Effect {
  BLIND(DISADVANTAGE, ADVANTAGE, 1);

  private final DiceRollModifier sourceModifier;
  private final DiceRollModifier targetModifier;
  private final int durationInTurns;

  public ActiveEffect activate() {
    return new ActiveEffect(this, durationInTurns);
  }
}
