package dbryla.game.yetanotherengine.domain.state;

import dbryla.game.yetanotherengine.domain.Action;
import dbryla.game.yetanotherengine.domain.IncorrectStateException;
import dbryla.game.yetanotherengine.domain.events.Event;
import dbryla.game.yetanotherengine.domain.operations.OperationResult;
import dbryla.game.yetanotherengine.domain.operations.UnsupportedGameOperationException;
import dbryla.game.yetanotherengine.domain.state.storage.StateStorage;
import dbryla.game.yetanotherengine.domain.state.storage.StepTracker;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class DefaultStateMachine implements StateMachine {

  private final StateStorage stateStorage;
  private final StepTracker stepTracker;

  public DefaultStateMachine(StepTracker stepTracker, StateStorage stateStorage) {
    this.stepTracker = stepTracker;
    this.stateStorage = stateStorage;
  }

  @Override
  public Optional<Subject> getNextSubject() {
    Optional<String> nextSubjectName = stepTracker.getNextSubjectName();
    if (nextSubjectName.isEmpty()) {
      return Optional.empty();
    }
    return stateStorage.findByName(nextSubjectName.get());
  }

  @Override
  public Set<Event> execute(Action action) {
    Set<Event> events = new HashSet<>();
    getNextSubject().ifPresent(subject -> {
      verifySource(action, subject);
      events.addAll(invokeOperation(action, subject));
      stepTracker.moveToNextSubject();
    });
    return events;
  }

  private Set<Event> invokeOperation(Action action, Subject subject) {
    try {
      return apply(action.getOperation().invoke(subject, action.getInstrument(), getTargets(action)));
    } catch (UnsupportedGameOperationException e) {
      throw new IncorrectStateException("Couldn't invoke operation on targets.", e);
    }
  }

  private void verifySource(Action action, Subject subject) {
    if (!action.getSourceName().equals(subject.getName())) {
      throw new IncorrectStateException("Can't invoke action from different subject then next one.");
    }
  }

  private Subject[] getTargets(Action action) {
    return action.getTargetNames()
        .stream()
        .map(stateStorage::findByName)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .toArray(Subject[]::new);
  }

  private Set<Event> apply(OperationResult operationResult) {
    if (operationResult == null) {
      return Set.of();
    }
    operationResult.getChangedSubjects().forEach(subject -> {
      stateStorage.save(subject);
      if (subject.isTerminated()) {
        stepTracker.removeSubject(subject.toIdentifier());
      }
    });
    return operationResult.getEmittedEvents();
  }

  @Override
  public boolean isInTerminalState() {
    return stepTracker.hasNoActionsToTrack();
  }
}
