package dbryla.game.yetanotherengine.domain.state.storage;

import dbryla.game.yetanotherengine.domain.state.SubjectIdentifier;
import java.util.Optional;

public interface StepTracker {

  Optional<String> getNextSubjectName();

  void removeSubject(SubjectIdentifier idenitifier);

  void moveToNextSubject();

  boolean hasNoActionsToTrack();
}
