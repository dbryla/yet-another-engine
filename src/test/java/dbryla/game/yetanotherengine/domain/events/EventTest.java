package dbryla.game.yetanotherengine.domain.events;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest {

  @Test
  void shouldReturnMessageOnToString() {
    String message = "message";
    Event event = new Event(message);

    String result = event.toString();

    assertThat(result).isEqualTo(message);
  }

  @Test
  void twoEventsAreEqualWhenTheyHaveTheSameMessage() {
    String message = "message";
    Event event1 = new Event(message);
    Event event2 = new Event(message);

    assertThat(event1).isEqualTo(event2);
  }
}