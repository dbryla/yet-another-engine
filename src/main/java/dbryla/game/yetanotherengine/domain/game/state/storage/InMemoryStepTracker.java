package dbryla.game.yetanotherengine.domain.game.state.storage;

import dbryla.game.yetanotherengine.domain.subject.Affiliation;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.SubjectProperties;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryStepTracker implements StepTracker {

  private final List<String> subjectsForAction;
  private final Map<Affiliation, Long> affiliationMap;
  private int nextSubjectIndex = 0;

  public InMemoryStepTracker(List<String> subjectsForAction, Map<Affiliation, Long> affiliationMap) {
    this.subjectsForAction = subjectsForAction;
    this.affiliationMap = affiliationMap;
  }

  @Override
  public Optional<String> getNextSubjectName() {
    if (subjectsForAction.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(subjectsForAction.get(nextSubjectIndex));
  }

  @Override
  public void removeSubject(Subject subject) {
    moveCursorLeft(subject.getName());
    subjectsForAction.remove(subject.getName());
    affiliationMap.computeIfPresent(subject.getAffiliation(), (key, value) -> value > 1 ? --value : null);
  }

  private void moveCursorLeft(String name) {
    int index = subjectsForAction.indexOf(name);
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
