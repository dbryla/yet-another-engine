package dbryla.game.yetanotherengine;

import java.util.*;

public class InMemoryStateMachine implements StateMachine {

  private final StateStorage stateStorage;
  private final StepTracker stepTracker;

  public InMemoryStateMachine(StepTracker stepTracker, StateStorage stateStorage) {
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
      apply(action.getOperation().invoke(subject, getTargets(action)));
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
