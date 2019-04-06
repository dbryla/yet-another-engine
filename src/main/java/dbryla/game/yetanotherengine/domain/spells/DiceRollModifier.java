package dbryla.game.yetanotherengine.domain.spells;

import dbryla.game.yetanotherengine.domain.DiceRoll;
import lombok.AllArgsConstructor;

import java.util.function.Supplier;

@AllArgsConstructor
public enum DiceRollModifier {

  DISADVANTAGE(() -> Math.min(DiceRoll.k20(), DiceRoll.k20())),
  ADVANTAGE(() -> Math.max(DiceRoll.k20(), DiceRoll.k20())),
  BLESSED(DiceRoll::k4),
  NONE(() -> 0);

  private final Supplier<Integer> diceRollModifier;

  public Integer getDiceRollModifier() {
    return diceRollModifier.get();
  }
}
