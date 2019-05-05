package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.effects.Effect;
import dbryla.game.yetanotherengine.domain.events.Event;
import dbryla.game.yetanotherengine.domain.events.EventFactory;
import dbryla.game.yetanotherengine.domain.subject.Condition;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EffectConsumerTest {

  @InjectMocks
  private EffectConsumer effectConsumer;
  @Mock
  private EventFactory eventFactory;

  @Test
  void shouldReturnEmptyOptionalIfNoEffectIsActive() {
    Subject subject = mock(Subject.class);
    when(subject.getConditions()).thenReturn(Set.of());

    OperationResult operationResult = effectConsumer.apply(subject);

    assertThat(operationResult.getChangedSubjects()).isEmpty();
  }


  @Test
  void shouldReturnEmptyOptionalIfEffectIsStillActive() {
    Subject subject = mock(Subject.class);
    when(subject.getConditions()).thenReturn(Set.of(new Condition(Effect.BLINDED, 2)));

    OperationResult operationResult = effectConsumer.apply(subject);

    assertThat(operationResult.getChangedSubjects()).isEmpty();
  }

  @Test
  void shouldReturnSubjectIfEffectExpires() {
    Subject subject = mock(Subject.class);
    Condition condition = Effect.BLINDED.activate(1);
    when(subject.getConditions()).thenReturn(Set.of(condition));
    when(subject.effectExpired(eq(Effect.BLINDED))).thenReturn(subject);
    when(eventFactory.effectExpiredEvent(any(), any())).thenReturn(new Event(""));

    OperationResult operationResult = effectConsumer.apply(subject);

    assertThat(operationResult.getChangedSubjects()).contains(subject);
  }

  @Test
  void shouldSendEventIfEffectExpires() {
    Subject subject = mock(Subject.class);
    Condition condition = Effect.BLINDED.activate(1);
    when(subject.getConditions()).thenReturn(Set.of(condition));
    when(subject.effectExpired(eq(Effect.BLINDED))).thenReturn(subject);
    when(eventFactory.effectExpiredEvent(any(), any())).thenReturn(new Event(""));

    effectConsumer.apply(subject);

    verify(eventFactory).effectExpiredEvent(eq(subject), eq(Effect.BLINDED));
  }
}