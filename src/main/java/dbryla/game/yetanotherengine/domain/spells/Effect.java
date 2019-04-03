package dbryla.game.yetanotherengine.domain.spells;

import dbryla.game.yetanotherengine.domain.DiceRollModifier;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static dbryla.game.yetanotherengine.domain.DiceRollModifier.ADVANTAGE;
import static dbryla.game.yetanotherengine.domain.DiceRollModifier.DISADVANTAGE;

@AllArgsConstructor
@Getter
public enum Effect {
  BLIND(DISADVANTAGE, ADVANTAGE, 1);

  private final DiceRollModifier ownerAsASource;
  private final DiceRollModifier ownerAsATarget;
  private final int durationInTurns;

}
