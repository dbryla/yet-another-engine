package dbryla.game.yetanotherengine;

import java.util.Optional;

public interface StateMachine {

  Optional<Subject> getNextSubject();

  StateMachine execute(Action action);

  boolean isInTerminalState();
}
