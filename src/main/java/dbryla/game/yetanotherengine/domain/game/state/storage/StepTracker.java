package dbryla.game.yetanotherengine.domain.game.state.storage;

import dbryla.game.yetanotherengine.domain.game.state.SubjectIdentifier;
import java.util.Optional;

public interface StepTracker {

  Optional<String> getNextSubjectName();

  void removeSubject(SubjectIdentifier identifier);

  void moveToNextSubject();

  boolean hasNoActionsToTrack();
}
