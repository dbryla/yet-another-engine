package dbryla.game.yetanotherengine;

public class IncorrectStateException extends RuntimeException {

  private final String message;

  public IncorrectStateException(String message) {
    this.message = message;
  }
}
