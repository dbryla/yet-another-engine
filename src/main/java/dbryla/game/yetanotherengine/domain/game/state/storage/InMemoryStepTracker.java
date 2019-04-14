package dbryla.game.yetanotherengine.domain.game.state.storage;

import dbryla.game.yetanotherengine.domain.game.state.SubjectIdentifier;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryStepTracker implements StepTracker {

  private final List<SubjectIdentifier> subjectsForAction;
  private final Map<String, Long> affiliationMap;
  private int nextSubjectIndex = 0;

  public InMemoryStepTracker(List<SubjectIdentifier> subjectsForAction, Map<String, Long> affiliationMap) {
    this.subjectsForAction = subjectsForAction;
    this.affiliationMap = affiliationMap;
  }

  @Override
  public Optional<String> getNextSubjectName() {
    if (subjectsForAction.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(subjectsForAction.get(nextSubjectIndex).getName());
  }

  @Override
  public void removeSubject(SubjectIdentifier identifier) {
    moveCursorLeft(identifier);
    subjectsForAction.remove(identifier);
    affiliationMap.computeIfPresent(identifier.getAffiliation(), (key, value) -> value > 1 ? --value : null);
  }

  private void moveCursorLeft(SubjectIdentifier identifier) {
    int index = subjectsForAction.indexOf(identifier);
    if (index != -1 && index <= nextSubjectIndex) {
      nextSubjectIndex--;
    }
  }

  @Override
  public void moveToNextSubject() {
    nextSubjectIndex++;
    if (nextSubjectIndex == subjectsForAction.size()) {
      nextSubjectIndex = 0;
    }
  }

  @Override
  public boolean hasNoActionsToTrack() {
    return subjectsForAction.isEmpty() || affiliationMap.size() == 1;
  }
}
