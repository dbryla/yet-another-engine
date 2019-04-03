package dbryla.game.yetanotherengine.domain;

import lombok.AllArgsConstructor;

import java.util.function.Supplier;

@AllArgsConstructor
public enum DiceRollModifier {

  DISADVANTAGE(() -> Math.min(DiceRoll.k20(), DiceRoll.k20())),
  ADVANTAGE(() -> Math.max(DiceRoll.k20(), DiceRoll.k20()));

  private final Supplier<Integer> diceRollModifier;

  public Integer getDiceRollModifier() {
    return diceRollModifier.get();
  }
}
