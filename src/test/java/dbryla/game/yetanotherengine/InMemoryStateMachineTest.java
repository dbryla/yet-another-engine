package dbryla.game.yetanotherengine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InMemoryStateMachineTest {

  @Mock
  private StateStorage stateStorage;

  @Mock
  private StepTracker stepTracker;

  private static final String SUBJECT_1_NAME = "subject1";
  private static final String SUBJECT_2_NAME = "subject2";


  @Test
  void shouldExecuteActionOnNextSubject() throws UnsupportedGameOperationException {
    Subject subject = givenSubjectOne();
    Operation operation = mock(Operation.class);
    Action action = new Action(SUBJECT_1_NAME, Collections.emptyList(), operation);
    when(stepTracker.getNextSubjectName()).thenReturn(Optional.of(SUBJECT_1_NAME));
    when(stateStorage.findByName(eq(SUBJECT_1_NAME))).thenReturn(Optional.of(subject));
    StateMachine stateMachine = new InMemoryStateMachine(stepTracker, stateStorage);

    stateMachine.execute(action);

    verify(operation).invoke(eq(subject));
  }

  @Test
  void shouldThrowExceptionWhenExecutingActionFromDifferentThanNextSubject() {
    Subject subject = givenSubjectOne();
    Action action = new Action(SUBJECT_2_NAME, "", null);
    when(stepTracker.getNextSubjectName()).thenReturn(Optional.of(SUBJECT_1_NAME));
    when(stateStorage.findByName(eq(SUBJECT_1_NAME))).thenReturn(Optional.of(subject));
    StateMachine stateMachine = new InMemoryStateMachine(stepTracker, stateStorage);

    assertThrows(IncorrectStateException.class, () -> stateMachine.execute(action));
  }

  private Subject givenSubjectOne() {
    Subject subject = mock(Subject.class);
    when(subject.getName()).thenReturn(SUBJECT_1_NAME);
    return subject;
  }

  @Test
  void shouldRemoveSubjectIfWasTerminated() throws UnsupportedGameOperationException {
    Subject subject = givenSubjectOne();
    when(subject.isTerminated()).thenReturn(true);
    Operation operation = mock(Operation.class);
    Action action = new Action(SUBJECT_1_NAME, Collections.emptyList(), operation);
    when(operation.invoke(eq(subject))).thenReturn(Set.of(subject));
    when(stepTracker.getNextSubjectName()).thenReturn(Optional.of(SUBJECT_1_NAME));
    when(stateStorage.findByName(SUBJECT_1_NAME)).thenReturn(Optional.of(subject));
    StateMachine stateMachine = new InMemoryStateMachine(stepTracker, stateStorage);

    stateMachine.execute(action);

    verify(stepTracker).removeSubject(any());
  }

  @Test
  void shouldMoveCursorToNextSubjectAfterExecutionOfAction() {
    Subject subject1 = givenSubjectOne();
    Subject subject2 = mock(Subject.class);
    Operation operation = mock(Operation.class);
    Action action = new Action(SUBJECT_1_NAME, Collections.emptyList(), operation);
    when(stepTracker.getNextSubjectName()).thenReturn(Optional.of(SUBJECT_1_NAME));
    lenient().when(stateStorage.findByName(eq(SUBJECT_1_NAME))).thenReturn(Optional.of(subject1));
    lenient().when(stateStorage.findByName(eq(SUBJECT_2_NAME))).thenReturn(Optional.of(subject2));
    StateMachine stateMachine = new InMemoryStateMachine(stepTracker, stateStorage);

    stateMachine.execute(action);

    verify(stepTracker).moveToNextSubject();
  }

  @Test
  void shouldExecuteActionOnEveryTarget() throws UnsupportedGameOperationException {
    Subject subject = givenSubjectOne();
    String target1Name = "target_name1";
    Subject target1 = mock(Subject.class);
    String target2Name = "target_name2";
    Subject target2 = mock(Subject.class);
    Operation operation = mock(Operation.class);
    when(stepTracker.getNextSubjectName()).thenReturn(Optional.of(SUBJECT_1_NAME));
    lenient().when(stateStorage.findByName(eq(SUBJECT_1_NAME))).thenReturn(Optional.of(subject));
    lenient().when(stateStorage.findByName(eq(target1Name))).thenReturn(Optional.of(target1));
    lenient().when(stateStorage.findByName(eq(target2Name))).thenReturn(Optional.of(target2));
    Action action = new Action(SUBJECT_1_NAME, List.of(target1Name, target2Name), operation);
    StateMachine stateMachine = new InMemoryStateMachine(stepTracker, stateStorage);

    stateMachine.execute(action);

    verify(operation).invoke(eq(subject), eq(target1), eq(target2));
  }

  @Test
  void shouldNotReturnNextSubjectWhenThereIsNoAction() {
    StateMachine stateMachine = new InMemoryStateMachine(stepTracker, stateStorage);

    assertThat(stateMachine.getNextSubject()).isNotPresent();
  }

  @Test
  void shouldNotTerminateIfMoreThanOneSubjectToActionRemains() {
    StateMachine stateMachine = new InMemoryStateMachine(stepTracker, stateStorage);

    assertThat(stateMachine.isInTerminalState()).isFalse();
  }

}