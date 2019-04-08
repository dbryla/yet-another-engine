package dbryla.game.yetanotherengine.domain;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.function.IntUnaryOperator;

@Slf4j
public class DiceRoll {

  private static final Random random = new Random();

  public static int k4() {
    int diceRoll = random.nextInt(4) + 1;
    log.trace("k4: " + diceRoll);
    return diceRoll;
  }

  public static int k6() {
    int diceRoll = random.nextInt(6) + 1;
    log.trace("k6: " + diceRoll);
    return diceRoll;
  }

  public static int k8() {
    int diceRoll = random.nextInt(8) + 1;
    log.trace("k8: " + diceRoll);
    return diceRoll;
  }

  public static int k10() {
    int diceRoll = random.nextInt(10) + 1;
    log.trace("k10: " + diceRoll);
    return diceRoll;
  }

  public static int k12() {
    int diceRoll = random.nextInt(12) + 1;
    log.trace("k12: " + diceRoll);
    return diceRoll;
  }

  public static int k20() {
    int diceRoll = random.nextInt(20) + 1;
    log.trace("k20: " + diceRoll);
    return diceRoll;
  }

  public static int of(int hitDice) {
    switch (hitDice) {
      case 4:
        return k4();
      case 6:
        return k6();
      case 8:
        return k8();
      case 10:
        return k10();
      case 12:
        return k12();
      case 20:
        return k20();
    }
    throw new IllegalArgumentException("Unsupported hit dice " + hitDice);
  }
}
