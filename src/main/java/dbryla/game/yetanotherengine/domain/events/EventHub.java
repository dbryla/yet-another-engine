package dbryla.game.yetanotherengine.domain.events;

import dbryla.game.yetanotherengine.domain.subject.Subject;

public interface EventHub {

  void send(Long gameId, Event event);

  void notifySubjectAboutNextMove(Long gameId, Subject subject);
}
