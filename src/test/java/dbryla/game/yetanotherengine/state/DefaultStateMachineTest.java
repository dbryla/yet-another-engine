package dbryla.game.yetanotherengine.state;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dbryla.game.yetanotherengine.domain.Action;
import dbryla.game.yetanotherengine.domain.Instrument;
import dbryla.game.yetanotherengine.domain.state.DefaultStateMachine;
import dbryla.game.yetanotherengine.domain.IncorrectStateException;
import dbryla.game.yetanotherengine.domain.operations.Operation;
import dbryla.game.yetanotherengine.domain.state.StateMachine;
import dbryla.game.yetanotherengine.domain.state.storage.StateStorage;
import dbryla.game.yetanotherengine.domain.state.storage.StepTracker;
import dbryla.game.yetanotherengine.domain.subjects.classes.Subject;
import dbryla.game.yetanotherengine.domain.operations.UnsupportedGameOperationException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import dbryla.game.yetanotherengine.domain.subjects.equipment.Weapon;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultStateMachineTest {

  @Mock
  private StateStorage stateStorage;

  @Mock
  private StepTracker stepTracker;

  private static final String SUBJECT_1_NAME = "subject1";
  private static final String SUBJECT_2_NAME = "subject2";
  private final static Instrument TEST_INSTRUMENT = new Instrument(Weapon.SHORTSWORD);

  @Test
  void shouldExecuteActionOnNextSubject() throws UnsupportedGameOperationException {
    Subject subject = givenSubjectOne();
    Operation operation = mock(Operation.class);
    Action action = new Action(SUBJECT_1_NAME, Collections.emptyList(), operation, TEST_INSTRUMENT);
    when(stepTracker.getNextSubjectName()).thenReturn(Optional.of(SUBJECT_1_NAME));
    when(stateStorage.findByName(eq(SUBJECT_1_NAME))).thenReturn(Optional.of(subject));
    StateMachine stateMachine = new DefaultStateMachine(stepTracker, stateStorage);

    stateMachine.execute(action);

    verify(operation).invoke(eq(subject), eq(TEST_INSTRUMENT));
  }

  @Test
  void shouldThrowExceptionWhenExecutingActionFromDifferentThanNextSubject() {
    Subject subject = givenSubjectOne();
    Action action = new Action(SUBJECT_2_NAME, "", null, TEST_INSTRUMENT);
    when(stepTracker.getNextSubjectName()).thenReturn(Optional.of(SUBJECT_1_NAME));
    when(stateStorage.findByName(eq(SUBJECT_1_NAME))).thenReturn(Optional.of(subject));
    StateMachine stateMachine = new DefaultStateMachine(stepTracker, stateStorage);

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
    Action action = new Action(SUBJECT_1_NAME, Collections.emptyList(), operation, TEST_INSTRUMENT);
    when(operation.invoke(eq(subject), eq(TEST_INSTRUMENT))).thenReturn(Set.of(subject));
    when(stepTracker.getNextSubjectName()).thenReturn(Optional.of(SUBJECT_1_NAME));
    when(stateStorage.findByName(SUBJECT_1_NAME)).thenReturn(Optional.of(subject));
    StateMachine stateMachine = new DefaultStateMachine(stepTracker, stateStorage);

    stateMachine.execute(action);

    verify(stepTracker).removeSubject(any());
  }

  @Test
  void shouldMoveCursorToNextSubjectAfterExecutionOfAction() {
    Subject subject1 = givenSubjectOne();
    Subject subject2 = mock(Subject.class);
    Operation operation = mock(Operation.class);
    Action action = new Action(SUBJECT_1_NAME, Collections.emptyList(), operation, TEST_INSTRUMENT);
    when(stepTracker.getNextSubjectName()).thenReturn(Optional.of(SUBJECT_1_NAME));
    lenient().when(stateStorage.findByName(eq(SUBJECT_1_NAME))).thenReturn(Optional.of(subject1));
    lenient().when(stateStorage.findByName(eq(SUBJECT_2_NAME))).thenReturn(Optional.of(subject2));
    StateMachine stateMachine = new DefaultStateMachine(stepTracker, stateStorage);

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
    Action action = new Action(SUBJECT_1_NAME, List.of(target1Name, target2Name), operation, TEST_INSTRUMENT);
    StateMachine stateMachine = new DefaultStateMachine(stepTracker, stateStorage);

    stateMachine.execute(action);

    verify(operation).invoke(eq(subject), eq(TEST_INSTRUMENT), eq(target1), eq(target2));
  }

  @Test
  void shouldNotReturnNextSubjectWhenThereIsNoAction() {
    StateMachine stateMachine = new DefaultStateMachine(stepTracker, stateStorage);

    assertThat(stateMachine.getNextSubject()).isNotPresent();
  }

  @Test
  void shouldNotTerminateIfMoreThanOneSubjectToActionRemains() {
    StateMachine stateMachine = new DefaultStateMachine(stepTracker, stateStorage);

    assertThat(stateMachine.isInTerminalState()).isFalse();
  }

}