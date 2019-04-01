package dbryla.game.yetanotherengine;

import java.util.Optional;

public interface StepTracker {

  Optional<String> getNextSubjectName();

  void removeSubject(SubjectIdenitifier idenitifier);

  void moveToNextSubject();

  boolean hasNoActionsToTrack();
}
