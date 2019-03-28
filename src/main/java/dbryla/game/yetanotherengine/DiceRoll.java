package dbryla.game.yetanotherengine;

import java.util.Random;

public class DiceRoll {

  private static final Random random = new Random();

  public static int k6() {
    return random.nextInt(6) + 1;
  }

  public static int k20() {
    return random.nextInt(20) + 1;
  }
}
