package dbryla.game.yetanotherengine;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class InMemoryStepTrackerTest {

  private static final SubjectIdentifier SUBJECT_1 = new SubjectIdentifier("subject1", "blue");
  private static final SubjectIdentifier SUBJECT_2 = new SubjectIdentifier("subject2", "green");

  @Test
  void shouldHasNoActionsToTrackIfOnlySubjectsWithTheSameAffiliationLeft() {
    StepTracker stepTracker = new InMemoryStepTracker(
        new LinkedList<>(List.of(SUBJECT_1, SUBJECT_2)),
        new HashMap<>(Map.of(SUBJECT_1.getAffiliation(), 1L, SUBJECT_2.getAffiliation(), 1L)));

    stepTracker.removeSubject(SUBJECT_1);

    assertThat(stepTracker.hasNoActionsToTrack()).isTrue();
  }

  @Test
  void shouldTerminateIfNoSubjectsToActionRemains() {
    StepTracker stepTracker = new InMemoryStepTracker(List.of(), Map.of());

    assertThat(stepTracker.hasNoActionsToTrack()).isTrue();
  }

  @Test
  void shouldMoveCursorToBeginningAfterExecutionOfLastAction() {
    StepTracker stepTracker = new InMemoryStepTracker(List.of(SUBJECT_1, SUBJECT_2), Map.of());
    stepTracker.moveToNextSubject();

    stepTracker.moveToNextSubject();

    Optional<String> nextSubjectName = stepTracker.getNextSubjectName();
    assertThat(nextSubjectName.isPresent()).isTrue();
    assertThat(nextSubjectName.get()).isEqualTo(SUBJECT_1.getName());
  }

}