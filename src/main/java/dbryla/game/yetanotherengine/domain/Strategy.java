package dbryla.game.yetanotherengine.domain;

import dbryla.game.yetanotherengine.domain.subjects.Subject;

public interface Strategy {

  int calculateInitiative(Subject subject);
}
