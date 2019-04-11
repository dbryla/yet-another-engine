package dbryla.game.yetanotherengine.domain.state;

import dbryla.game.yetanotherengine.domain.Action;
import dbryla.game.yetanotherengine.domain.events.Event;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import java.util.Optional;
import java.util.Set;

public interface StateMachine {

  Optional<Subject> getNextSubject();

  Set<Event> execute(Action action);

  boolean isInTerminalState();
}
