package dbryla.game.yetanotherengine.domain.subjects;

import dbryla.game.yetanotherengine.domain.DiceRoll;
import java.util.function.Supplier;

public enum Weapon {
  SHORTSWORD(DiceRoll::k6),
  GREATSWORD(() -> DiceRoll.k6() + DiceRoll.k6()),
  DAGGER(DiceRoll::k4),
  QUARTERSTAFF(DiceRoll::k6);

  private final Supplier<Integer> diceRoll;

  Weapon(Supplier<Integer> diceRoll) {
    this.diceRoll = diceRoll;
  }

  public int rollAttackDamage() {
    return diceRoll.get();
  }

}
