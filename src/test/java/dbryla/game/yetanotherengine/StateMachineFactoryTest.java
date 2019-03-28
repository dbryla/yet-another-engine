package dbryla.game.yetanotherengine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StateMachineFactoryTest {

  @Mock
  private Strategy strategy;

  private final StateMachineFactory stateMachineFactory = new StateMachineFactory();

  @Test
  void shouldOrderSubjectsByInitiativeAndAssignSubjectsStateAfterInitialization() {
    String subject1name = "subject1";
    Subject subject1 = mock(Subject.class);
    when(subject1.getName()).thenReturn(subject1name);
    String subject2name = "subject2";
    Subject subject2 = mock(Subject.class);
    when(subject2.getName()).thenReturn(subject2name);
    lenient().when(strategy.calculateInitiative(eq(subject1))).thenReturn(1);
    lenient().when(strategy.calculateInitiative(eq(subject2))).thenReturn(10);

    StateMachine stateMachine = stateMachineFactory.createInMemoryStateMachine(Set.of(subject1, subject2), strategy);

    assertThat(stateMachine.getNextSubject().get()).isEqualTo(subject2);
    assertThat(stateMachine.getSubjectsState()).contains(new SimpleEntry<>(subject1name, subject1), new SimpleEntry<>(subject2name, subject2));
  }

  @Test
  void shouldThrowExceptionWhileInitializingWithNoSubjects() {
    assertThrows(IncorrectStateException.class, () -> stateMachineFactory.createInMemoryStateMachine(Collections.emptySet(), strategy));
  }

  @Test
  void shouldThrowExceptionWhileInitializingWithNullSet() {
    assertThrows(IncorrectStateException.class, () -> stateMachineFactory.createInMemoryStateMachine(null, strategy));
  }

  @Test
  void shouldThrowExceptionWhileInitializingWithNullStrategy() {
    assertThrows(IncorrectStateException.class, () -> stateMachineFactory.createInMemoryStateMachine(Set.of(mock(Subject.class)), null));
  }

}