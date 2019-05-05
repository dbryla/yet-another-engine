package dbryla.game.yetanotherengine.domain.game.state;

import dbryla.game.yetanotherengine.domain.IncorrectStateException;
import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.game.Action;
import dbryla.game.yetanotherengine.domain.game.SubjectTurn;
import dbryla.game.yetanotherengine.domain.game.state.storage.StateStorage;
import dbryla.game.yetanotherengine.domain.game.state.storage.StepTracker;
import dbryla.game.yetanotherengine.domain.operations.*;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultStateMachineTest {

  @Mock
  private StateStorage stateStorage;

  @Mock
  private StepTracker stepTracker;

  @Mock
  private OperationFactory operationFactory;

  @Mock
  private EventHub eventHub;

  @Mock
  private EffectConsumer effectConsumer;

  private static final Long GAME_ID = 123L;
  private static final String SUBJECT_1_NAME = "subject1";
  private static final String SUBJECT_2_NAME = "subject2";
  private final static ActionData TEST_ACTION_DATA = new ActionData(Weapon.SHORTSWORD);
  private StateMachine stateMachine;

  @BeforeEach
  void setUp() {
    stateMachine = new DefaultStateMachine(GAME_ID, stepTracker, stateStorage, eventHub, effectConsumer, operationFactory);
  }

  @Test
  void shouldExecuteActionOnNextSubject() throws UnsupportedGameOperationException {
    Subject subject = givenSubjectOne();
    Action action = new Action(SUBJECT_1_NAME, Collections.emptyList(), OperationType.ATTACK, TEST_ACTION_DATA);
    when(stepTracker.getNextSubjectName()).thenReturn(Optional.of(SUBJECT_1_NAME));
    when(stateStorage.findByIdAndName(eq(GAME_ID), eq(SUBJECT_1_NAME))).thenReturn(Optional.of(subject));
    Operation attackOperation = mock(Operation.class);
    when(operationFactory.getOperation(OperationType.ATTACK)).thenReturn(attackOperation);

    stateMachine.execute(SubjectTurn.of(action));

    verify(attackOperation).invoke(eq(subject), eq(TEST_ACTION_DATA));
  }

  @Test
  void shouldThrowExceptionWhenExecutingActionFromDifferentThanNextSubject() {
    Subject subject = givenSubjectOne();
    Action action = new Action(SUBJECT_2_NAME, "", null, TEST_ACTION_DATA);
    when(stepTracker.getNextSubjectName()).thenReturn(Optional.of(SUBJECT_1_NAME));
    when(stateStorage.findByIdAndName(eq(GAME_ID), eq(SUBJECT_1_NAME))).thenReturn(Optional.of(subject));

    assertThrows(IncorrectStateException.class, () -> stateMachine.execute(SubjectTurn.of(action)));
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
    Action action = new Action(SUBJECT_1_NAME, Collections.emptyList(), OperationType.ATTACK, TEST_ACTION_DATA);
    Operation attackOperation = mock(Operation.class);
    when(operationFactory.getOperation(OperationType.ATTACK)).thenReturn(attackOperation);
    when(attackOperation.invoke(eq(subject), eq(TEST_ACTION_DATA))).thenReturn(new OperationResult(List.of(subject), List.of()));
    when(stepTracker.getNextSubjectName()).thenReturn(Optional.of(SUBJECT_1_NAME));
    when(stateStorage.findByIdAndName(eq(GAME_ID), eq(SUBJECT_1_NAME))).thenReturn(Optional.of(subject));

    stateMachine.execute(SubjectTurn.of(action));

    verify(stepTracker).removeSubject(any());
  }

  @Test
  void shouldMoveCursorToNextSubjectAfterExecutionOfAction() {
    Subject subject1 = givenSubjectOne();
    Subject subject2 = mock(Subject.class);
    Action action = new Action(SUBJECT_1_NAME, Collections.emptyList(), OperationType.ATTACK, TEST_ACTION_DATA);
    when(stepTracker.getNextSubjectName()).thenReturn(Optional.of(SUBJECT_1_NAME));
    lenient().when(stateStorage.findByIdAndName(eq(GAME_ID), eq(SUBJECT_1_NAME))).thenReturn(Optional.of(subject1));
    lenient().when(stateStorage.findByIdAndName(eq(GAME_ID), eq(SUBJECT_2_NAME))).thenReturn(Optional.of(subject2));
    when(operationFactory.getOperation(OperationType.ATTACK)).thenReturn(mock(Operation.class));

    stateMachine.execute(SubjectTurn.of(action));

    verify(stepTracker).moveToNextSubject();
  }

  @Test
  void shouldExecuteActionOnEveryTarget() throws UnsupportedGameOperationException {
    Subject subject = givenSubjectOne();
    String target1Name = "target_name1";
    Subject target1 = mock(Subject.class);
    String target2Name = "target_name2";
    Subject target2 = mock(Subject.class);
    when(stepTracker.getNextSubjectName()).thenReturn(Optional.of(SUBJECT_1_NAME));
    Action action = new Action(SUBJECT_1_NAME, List.of(target1Name, target2Name), OperationType.ATTACK, TEST_ACTION_DATA);
    lenient().when(stateStorage.findByIdAndName(eq(GAME_ID), eq(SUBJECT_1_NAME))).thenReturn(Optional.of(subject));
    lenient().when(stateStorage.findByIdAndName(eq(GAME_ID), eq(target1Name))).thenReturn(Optional.of(target1));
    lenient().when(stateStorage.findByIdAndName(eq(GAME_ID), eq(target2Name))).thenReturn(Optional.of(target2));
    Operation attackOperation = mock(Operation.class);
    when(operationFactory.getOperation(OperationType.ATTACK)).thenReturn(attackOperation);

    stateMachine.execute(SubjectTurn.of(action));

    verify(attackOperation).invoke(eq(subject), eq(TEST_ACTION_DATA), eq(target1), eq(target2));
  }

  @Test
  void shouldNotReturnNextSubjectWhenThereIsNoAction() {
    assertThat(stateMachine.getNextSubject()).isNotPresent();
  }

  @Test
  void shouldNotTerminateIfMoreThanOneSubjectToActionRemains() {
    assertThat(stateMachine.isInTerminalState()).isFalse();
  }

}