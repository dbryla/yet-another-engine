package dbryla.game.yetanotherengine;

import java.util.Optional;

public interface StepTracker {

  Optional<String> getNextSubjectName();

  void removeSubject(SubjectIdentifier idenitifier);

  void moveToNextSubject();

  boolean hasNoActionsToTrack();
}
