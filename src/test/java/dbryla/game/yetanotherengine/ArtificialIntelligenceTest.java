package dbryla.game.yetanotherengine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ArtificialIntelligenceTest {

  private static final String TARGET_NAME = "acquiredTarget";

  @Mock
  private Subject subject;

  @InjectMocks
  private ArtificialIntelligence artificialIntelligence;

  @Test
  void shouldReturnActionWithAcquiredTarget() {
    Subject target = mock(Subject.class);
    StateStorage stateStorage = givenStateStorageWithSingleTarget(target);

    Action action = artificialIntelligence.nextAction(stateStorage);

    assertThat(action.getTargetNames()).contains(TARGET_NAME);
  }

  private StateStorage givenStateStorageWithSingleTarget(Subject target) {
    when(subject.getName()).thenReturn("subject");
    StateStorage stateStorage = mock(StateStorage.class);
    when(target.getName()).thenReturn(TARGET_NAME);
    when(target.getAffiliation()).thenReturn("enemy");
    when(stateStorage.findAll()).thenReturn(List.of(target));
    return stateStorage;
  }

  @Test
  void shouldReturnNextActionWithSameAcquiredTarget() {
    Subject target = mock(Subject.class);
    StateStorage stateStorage = givenStateStorageWithSingleTarget(target);
    when(stateStorage.findByName(eq(TARGET_NAME))).thenReturn(Optional.of(target));
    when(target.isTerminated()).thenReturn(false);
    artificialIntelligence.nextAction(stateStorage);

    Action action = artificialIntelligence.nextAction(stateStorage);

    assertThat(action.getTargetNames()).contains(TARGET_NAME);
  }

  @Test
  void shouldThrowExceptionWhenTargetIsTerminatedAndCantFindNewOne() {
    Subject target = mock(Subject.class);
    StateStorage stateStorage = givenStateStorageWithSingleTarget(target);
    when(stateStorage.findByName(eq(TARGET_NAME))).thenReturn(Optional.of(target));
    when(target.isTerminated()).thenReturn(false).thenReturn(true);
    artificialIntelligence.nextAction(stateStorage);

    assertThrows(IncorrectStateException.class, () -> artificialIntelligence.nextAction(stateStorage));
  }
}