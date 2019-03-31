package dbryla.game.yetanotherengine;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class InMemoryStateMachine implements StateMachine {

  private final List<String> subjectsForAction;
  private final StateStorage stateStorage;
  private int nextSubjectIndex = 0;

  public InMemoryStateMachine(List<String> subjectsForAction, StateStorage stateStorage) {
    this.subjectsForAction = subjectsForAction;
    this.stateStorage = stateStorage;
  }

  @Override
  public Optional<Subject> getNextSubject() {
    if (subjectsForAction.isEmpty()) {
      return Optional.empty();
    }
    return stateStorage.findByName(subjectsForAction.get(nextSubjectIndex));
  }

  @Override
  public StateMachine execute(Action action) {
    getNextSubject().ifPresent(subject -> {
      verifySource(action, subject);
      invokeOperation(action, subject);
      moveToNextSubject();
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
        subjectsForAction.remove(subject.getName());
      }
    });
  }

  private void moveToNextSubject() {
    nextSubjectIndex++;
    if (nextSubjectIndex == subjectsForAction.size()) {
      nextSubjectIndex = 0;
    }
  }

  @Override
  public boolean isInTerminalState() {
    return subjectsForAction.size() == 1 || subjectsForAction.isEmpty();
  }
}
