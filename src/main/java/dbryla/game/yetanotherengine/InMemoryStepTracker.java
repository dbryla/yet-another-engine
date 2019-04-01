package dbryla.game.yetanotherengine;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryStepTracker implements StepTracker {

  private final List<SubjectIdenitifier> subjectsForAction;
  private final Map<String, Long> affiliationMap;
  private int nextSubjectIndex = 0;

  public InMemoryStepTracker(List<SubjectIdenitifier> subjectsForAction, Map<String, Long> affiliationMap) {
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
  public void removeSubject(SubjectIdenitifier idenitifier) {
    subjectsForAction.remove(idenitifier);
    affiliationMap.computeIfPresent(idenitifier.getAffiliation(), (key, value) -> value > 1 ? value-- : null);
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
