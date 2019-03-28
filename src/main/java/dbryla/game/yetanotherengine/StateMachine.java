package dbryla.game.yetanotherengine;

import java.util.Map;
import java.util.Optional;

public interface StateMachine {

  Optional<Subject> getNextSubject();

  StateMachine execute(Action action);

  Map<String, Subject> getSubjectsState();

  boolean isInTerminalState();
}
