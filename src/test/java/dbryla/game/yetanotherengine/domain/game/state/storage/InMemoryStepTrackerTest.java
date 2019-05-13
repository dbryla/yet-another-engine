package dbryla.game.yetanotherengine.domain.game.state.storage;

import dbryla.game.yetanotherengine.domain.TestData;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.SubjectProperties;
import org.junit.jupiter.api.Test;

import java.util.*;

import static dbryla.game.yetanotherengine.domain.subject.Affiliation.ENEMIES;
import static dbryla.game.yetanotherengine.domain.subject.Affiliation.PLAYERS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InMemoryStepTrackerTest {

  private static final String SUBJECT_1 = "subject1";
  private static final String SUBJECT_2 = "subject2";

  @Test
  void shouldHasNoActionsToTrackIfOnlySubjectsWithTheSameAffiliationLeft() {
    StepTracker stepTracker = new InMemoryStepTracker(
        new LinkedList<>(List.of(SUBJECT_1, SUBJECT_2)),
        new HashMap<>(Map.of(PLAYERS, 1L, ENEMIES, 1L)));
    Subject subject = mock(Subject.class);
    when(subject.getName()).thenReturn(SUBJECT_1);
    when(subject.getAffiliation()).thenReturn(PLAYERS);

    stepTracker.removeSubject(subject);

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
    assertThat(nextSubjectName.get()).isEqualTo(SUBJECT_1);
  }

}