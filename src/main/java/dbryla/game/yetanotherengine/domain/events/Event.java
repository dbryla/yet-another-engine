package dbryla.game.yetanotherengine.domain.events;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@AllArgsConstructor
public class Event {

  private final String message;

  @Override
  public String toString() {
    return message.replace("  ", " ");
  }
}
