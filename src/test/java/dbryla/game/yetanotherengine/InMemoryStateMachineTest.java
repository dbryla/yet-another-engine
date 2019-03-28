package dbryla.game.yetanotherengine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class InMemoryStateMachineTest {

  private static final String SUBJECT_1_NAME = "subject1";
  private static final String SUBJECT_2_NAME = "subject2";

  @Test
  void shouldExecuteActionOnNextSubject() {
    Subject subject = givenSubjectOne();
    Action action = mock(Action.class);
    when(action.getSourceName()).thenReturn(SUBJECT_1_NAME);
    StateMachine stateMachine = new InMemoryStateMachine(List.of(SUBJECT_1_NAME), Map.of(SUBJECT_1_NAME, subject));

    stateMachine.execute(action);

    verify(action).invoke(eq(subject));
  }

  @Test
  void shouldThrowExceptionWhenExecutingActionFromDifferentThanNextSubject() {
    Subject subject = givenSubjectOne();
    Action action = mock(Action.class);
    when(action.getSourceName()).thenReturn(SUBJECT_2_NAME);
    StateMachine stateMachine = new InMemoryStateMachine(List.of(SUBJECT_1_NAME), Map.of(SUBJECT_1_NAME, subject));

    assertThrows(IncorrectStateException.class, () -> stateMachine.execute(action));
  }

  private Subject givenSubjectOne() {
    Subject subject = mock(Subject.class);
    when(subject.getName()).thenReturn(SUBJECT_1_NAME);
    return subject;
  }

  @Test
  void shouldMoveCursorToNextSubjectAfterExecutionOfAction() {
    Subject subject1 = givenSubjectOne();
    Subject subject2 = givenSubjectTwo();
    Action action = mock(Action.class);
    when(action.getSourceName()).thenReturn(SUBJECT_1_NAME);
    StateMachine stateMachine = new InMemoryStateMachine(
        List.of(SUBJECT_1_NAME, SUBJECT_2_NAME),
        Map.of(SUBJECT_1_NAME, subject1, SUBJECT_2_NAME, subject2));

    stateMachine.execute(action);

    assertThat(stateMachine.getNextSubject().get()).isEqualTo(subject2);
  }

  private Subject givenSubjectTwo() {
    Subject subject2 = mock(Subject.class);
    when(subject2.getName()).thenReturn(SUBJECT_2_NAME);
    return subject2;
  }

  @Test
  void shouldNotReturnNextSubjectWhenThereIsNoAction() {
    StateMachine stateMachine = new InMemoryStateMachine(Collections.emptyList(), null);

    assertThat(stateMachine.getNextSubject()).isNotPresent();
  }


  @Test
  void shouldMoveCursorToBeginningAfterExecutionOfLastAction() {
    Subject subject1 = givenSubjectOne();
    Subject subject2 = givenSubjectTwo();
    Action action1 = mock(Action.class);
    when(action1.getSourceName()).thenReturn(SUBJECT_1_NAME);
    Action action2 = mock(Action.class);
    when(action2.getSourceName()).thenReturn(SUBJECT_2_NAME);
    StateMachine stateMachine = new InMemoryStateMachine(
        List.of(SUBJECT_1_NAME, SUBJECT_2_NAME),
        Map.of(SUBJECT_1_NAME, subject1, SUBJECT_2_NAME, subject2)
    ).execute(action1);

    stateMachine.execute(action2);

    assertThat(stateMachine.getNextSubject().get()).isEqualTo(subject1);
  }

  @Test
  void shouldExecuteActionOnEveryTarget() {
    Subject subject = givenSubjectOne();
    String target1Name = "target_name1";
    Subject target1 = mock(Subject.class);
    when(target1.getName()).thenReturn(target1Name);
    String target2Name = "target_name2";
    Subject target2 = mock(Subject.class);
    when(target2.getName()).thenReturn(target2Name);
    Action action = mock(Action.class);
    when(action.getSourceName()).thenReturn(SUBJECT_1_NAME);
    when(action.getTargetNames()).thenReturn(List.of(target1Name, target2Name));
    StateMachine stateMachine = new InMemoryStateMachine(
        List.of(SUBJECT_1_NAME, target1Name, target2Name),
        Map.of(SUBJECT_1_NAME, subject, target1Name, target1, target2Name, target2));

    stateMachine.execute(action);

    verify(action).invoke(eq(subject), eq(target1), eq(target2));
  }

  @Test
  void shouldTerminateIfNoSubjectsToActionRemains() {
    StateMachine stateMachine = new InMemoryStateMachine(Collections.emptyList(), null);

    assertThat(stateMachine.isInTerminalState()).isTrue();
  }

  @Test
  void shouldNotTerminateIfSubjectsToActionRemains() {
    StateMachine stateMachine = new InMemoryStateMachine(List.of(SUBJECT_1_NAME), null);

    assertThat(stateMachine.isInTerminalState()).isFalse();
  }

  @Test
  void shouldTerminateIfAllSubjectsWereTerminated() {
    Subject subject = givenSubjectOne();
    when(subject.isTerminated()).thenReturn(true);
    Action action = mock(Action.class);
    when(action.getSourceName()).thenReturn(SUBJECT_1_NAME);
    when(action.invoke(eq(subject))).thenReturn(Set.of(subject));
    StateMachine stateMachine = new InMemoryStateMachine(new LinkedList<>(List.of(SUBJECT_1_NAME)), new HashMap<>(Map.of(SUBJECT_1_NAME, subject)));

    stateMachine.execute(action);

    assertThat(stateMachine.isInTerminalState()).isTrue();
  }
}