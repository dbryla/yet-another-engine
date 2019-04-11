package dbryla.game.yetanotherengine.domain.events;

import dbryla.game.yetanotherengine.domain.subjects.Subject;

public interface EventHub {

  void send(Event event, Long gameId);

  void nextMove(Subject subject, Long gameId);
}
