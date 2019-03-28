package dbryla.game.yetanotherengine;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class InMemoryStateMachine implements StateMachine {

  private List<String> subjectsForAction;
  private final Map<String, Subject> subjectsState;
  private int nextSubjectIndex = 0;

  public InMemoryStateMachine(List<String> subjectsForAction, Map<String, Subject> subjectsState) {
    this.subjectsForAction = subjectsForAction;
    this.subjectsState = subjectsState;
  }

  @Override
  public Optional<Subject> getNextSubject() {
    if (subjectsForAction.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(subjectsState.get(subjectsForAction.get(nextSubjectIndex)));
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
        .map(subjectsState::get)
        .toArray(Subject[]::new);
  }

  private void apply(Set<Subject> changedSubjects) {
    changedSubjects.forEach(subject -> {
      subjectsState.put(subject.getName(), subject);
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
  public Map<String, Subject> getSubjectsState() {
    return subjectsState;
  }

  @Override
  public boolean isInTerminalState() {
    return subjectsForAction.size() == 1 || subjectsForAction.isEmpty();
  }
}
