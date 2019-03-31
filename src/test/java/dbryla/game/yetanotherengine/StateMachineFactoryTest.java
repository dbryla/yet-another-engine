package dbryla.game.yetanotherengine;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StateMachineFactoryTest {

  @Mock
  private Strategy strategy;

  @Mock
  private StateStorage stateStorage;

  @InjectMocks
  private StateMachineFactory stateMachineFactory;

  @Test
  void shouldCalculateInitiativesBasedOnStrategy() {
    Subject subject1 = mock(Subject.class);
    Subject subject2 = mock(Subject.class);
    when(stateStorage.findAll()).thenReturn(List.of(subject1, subject2));

    stateMachineFactory.createInMemoryStateMachine(strategy);

    verify(strategy).calculateInitiative(eq(subject1));
    verify(strategy).calculateInitiative(eq(subject2));
  }

  @Test
  void shouldInitializeStateMachineInNotTerminalState() {
    Subject subject1 = mock(Subject.class);
    Subject subject2 = mock(Subject.class);
    when(stateStorage.findAll()).thenReturn(List.of(subject1, subject2));

    StateMachine stateMachine = stateMachineFactory.createInMemoryStateMachine(strategy);

    assertThat(stateMachine.isInTerminalState()).isFalse();
  }

  @Test
  void shouldThrowExceptionWhileInitializingWithNullStrategy() {
    assertThrows(IncorrectStateException.class, () -> stateMachineFactory.createInMemoryStateMachine(null));
  }

}