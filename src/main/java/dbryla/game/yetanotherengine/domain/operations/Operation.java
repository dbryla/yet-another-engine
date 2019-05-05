package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.subject.Subject;

public interface Operation {
  OperationResult invoke(Subject source, ActionData actionData, Subject... targets) throws UnsupportedGameOperationException;
}
