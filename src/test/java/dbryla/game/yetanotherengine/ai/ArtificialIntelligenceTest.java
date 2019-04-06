package dbryla.game.yetanotherengine.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dbryla.game.yetanotherengine.domain.Action;
import dbryla.game.yetanotherengine.domain.IncorrectStateException;
import dbryla.game.yetanotherengine.domain.ai.ArtificialIntelligence;
import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.state.storage.StateStorage;
import dbryla.game.yetanotherengine.domain.subjects.classes.Subject;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ArtificialIntelligenceTest {

  private static final String SUBJECT_NAME = "subject";
  private static final String TARGET_NAME = "acquiredTarget";

  @Mock
  private EventHub eventHub;

  @Mock
  private StateStorage stateStorage;

  @InjectMocks
  private ArtificialIntelligence artificialIntelligence;

  @Test
  void shouldReturnActionWithAcquiredTarget() {
    Subject subject = mock(Subject.class);
    Subject target = mock(Subject.class);
    givenStateStorageWithSingleTarget(subject, target);
    artificialIntelligence.initSubject(subject);

    Action action = artificialIntelligence.attackAction(SUBJECT_NAME);

    assertThat(action.getTargetNames()).contains(TARGET_NAME);
  }

  private void givenStateStorageWithSingleTarget(Subject subject, Subject target) {
    when(subject.getName()).thenReturn(SUBJECT_NAME);
    when(target.getName()).thenReturn(TARGET_NAME);
    when(target.getAffiliation()).thenReturn("enemy");
    when(stateStorage.findAll()).thenReturn(List.of(target));
  }

  @Test
  void shouldReturnNextActionWithSameAcquiredTarget() {
    Subject subject = mock(Subject.class);
    Subject target = mock(Subject.class);
    givenStateStorageWithSingleTarget(subject, target);
    when(stateStorage.findByName(eq(TARGET_NAME))).thenReturn(Optional.of(target));
    when(target.isTerminated()).thenReturn(false);
    artificialIntelligence.initSubject(subject);
    artificialIntelligence.attackAction(SUBJECT_NAME);

    Action action = artificialIntelligence.attackAction(SUBJECT_NAME);

    assertThat(action.getTargetNames()).contains(TARGET_NAME);
  }

  @Test
  void shouldThrowExceptionWhenTargetIsTerminatedAndCantFindNewOne() {
    Subject subject = mock(Subject.class);
    Subject target = mock(Subject.class);
    givenStateStorageWithSingleTarget(subject, target);
    when(stateStorage.findByName(eq(TARGET_NAME))).thenReturn(Optional.of(target));
    when(target.isTerminated()).thenReturn(false).thenReturn(true);
    artificialIntelligence.initSubject(subject);
    artificialIntelligence.attackAction(SUBJECT_NAME);

    assertThrows(IncorrectStateException.class, () -> artificialIntelligence.attackAction(SUBJECT_NAME));
  }
}