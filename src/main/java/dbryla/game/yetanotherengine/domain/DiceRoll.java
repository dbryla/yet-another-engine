package dbryla.game.yetanotherengine.domain;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class DiceRoll {

  private static final Random random = new Random();

  public static int k6() {
    int diceRoll = random.nextInt(6) + 1;
    log.trace("k6: " + diceRoll);
    return diceRoll;
  }

  public static int k20() {
    int diceRoll = random.nextInt(20) + 1;
    log.trace("k20: " + diceRoll);
    return diceRoll;
  }

  public static int k4() {
    int diceRoll = random.nextInt(4) + 1;
    log.trace("k4: " + diceRoll);
    return diceRoll;
  }

  public static int k10() {
    int diceRoll = random.nextInt(10) + 1;
    log.trace("k10: " + diceRoll);
    return diceRoll;
  }
}
