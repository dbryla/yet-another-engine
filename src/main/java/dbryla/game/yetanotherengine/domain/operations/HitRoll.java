package dbryla.game.yetanotherengine.domain.operations;

import lombok.Getter;


@Getter
class HitRoll {
  private final int original;
  private int actual;

  HitRoll(int original, int modifier) {
    this.original = original;
    this.actual = original + modifier;
  }

  void addModifier(int modifier) {
    this.actual += modifier;
  }
}
