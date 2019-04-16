package dbryla.game.yetanotherengine.domain.game.state;

import dbryla.game.yetanotherengine.domain.game.SubjectTurn;
import dbryla.game.yetanotherengine.domain.subject.Subject;

import java.util.Optional;

public interface StateMachine {

  Optional<Subject> getNextSubject();

  void execute(SubjectTurn subjectTurn);

  boolean isInTerminalState();
}
