package dbryla.game.yetanotherengine.domain;

public class IncorrectStateException extends RuntimeException {

  public IncorrectStateException(String message) {
    super(message);
  }

  public IncorrectStateException(String message, Throwable cause) {
    super(message, cause);
  }
}
