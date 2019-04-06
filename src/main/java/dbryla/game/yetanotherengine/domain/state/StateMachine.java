package dbryla.game.yetanotherengine.domain.state;

import dbryla.game.yetanotherengine.domain.Action;
import dbryla.game.yetanotherengine.domain.subjects.classes.Subject;
import java.util.Optional;

public interface StateMachine {

  Optional<Subject> getNextSubject();

  StateMachine execute(Action action);

  boolean isInTerminalState();
}
