package dbryla.game.yetanotherengine;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryStepTrackerTest {

  private static final SubjectIdenitifier SUBJECT_1 = new SubjectIdenitifier("subject1", "blue");
  private static final SubjectIdenitifier SUBJECT_2 = new SubjectIdenitifier("subject2", "green");

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