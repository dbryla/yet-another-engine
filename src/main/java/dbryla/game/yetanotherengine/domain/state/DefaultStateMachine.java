package dbryla.game.yetanotherengine.domain.state;

import dbryla.game.yetanotherengine.domain.Action;
import dbryla.game.yetanotherengine.domain.IncorrectStateException;
import dbryla.game.yetanotherengine.domain.operations.UnsupportedGameOperationException;
import dbryla.game.yetanotherengine.domain.state.storage.StateStorage;
import dbryla.game.yetanotherengine.domain.state.storage.StepTracker;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import java.util.*;

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
  public StateMachine execute(Action action) {
    getNextSubject().ifPresent(subject -> {
      verifySource(action, subject);
      invokeOperation(action, subject);
      stepTracker.moveToNextSubject();
    });
    return this;
  }

  @SuppressWarnings("unchecked")
  private void invokeOperation(Action action, Subject subject) {
    try {
      apply(action.getOperation().invoke(subject, action.getInstrument(), getTargets(action)));
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

  private void apply(Set<Subject> changedSubjects) {
    changedSubjects.forEach(subject -> {
      stateStorage.save(subject);
      if (subject.isTerminated()) {
        stepTracker.removeSubject(subject.toIdentifier());
      }
    });
  }

  @Override
  public boolean isInTerminalState() {
    return stepTracker.hasNoActionsToTrack();
  }
}
