package dbryla.game.yetanotherengine.domain.events;

public interface EventLog {

  void send(Event event);
}
