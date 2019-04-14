package dbryla.game.yetanotherengine.domain.dice;

public interface HitDiceRollModifier {

  int apply(int originalDiceRoll);

  boolean canModifyOriginalHitRoll();
}
