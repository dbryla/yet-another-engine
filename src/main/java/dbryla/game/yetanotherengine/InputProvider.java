package dbryla.game.yetanotherengine;

import dbryla.game.yetanotherengine.domain.subjects.Subject;

public interface InputProvider {

  void askForAction(Subject subject, Long gameId);
}
