package dbryla.game.yetanotherengine.domain.operations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dbryla.game.yetanotherengine.domain.events.Event;
import dbryla.game.yetanotherengine.domain.events.EventsFactory;
import dbryla.game.yetanotherengine.domain.effects.Effect;
import dbryla.game.yetanotherengine.domain.subject.ActiveEffect;

import java.util.Set;

import dbryla.game.yetanotherengine.domain.subject.Subject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EffectConsumerTest {

  @InjectMocks
  private EffectConsumer effectConsumer;
  @Mock
  private EventsFactory eventsFactory;

  @Test
  void shouldReturnEmptyOptionalIfNoEffectIsActive() {
    Subject subject = mock(Subject.class);
    when(subject.getActiveEffects()).thenReturn(Set.of());

    OperationResult operationResult = effectConsumer.apply(subject);

    assertThat(operationResult.getChangedSubjects()).isEmpty();
  }


  @Test
  void shouldReturnEmptyOptionalIfEffectIsStillActive() {
    Subject subject = mock(Subject.class);
    when(subject.getActiveEffects()).thenReturn(Set.of(new ActiveEffect(Effect.BLIND, 2)));

    OperationResult operationResult = effectConsumer.apply(subject);

    assertThat(operationResult.getChangedSubjects()).isEmpty();
  }

  @Test
  void shouldReturnSubjectIfEffectExpires() {
    Subject subject = mock(Subject.class);
    ActiveEffect activeEffect = Effect.BLIND.activate();
    when(subject.getActiveEffects()).thenReturn(Set.of(activeEffect));
    when(subject.effectExpired(eq(Effect.BLIND))).thenReturn(subject);
    when(eventsFactory.effectExpiredEvent(any(), any())).thenReturn(new Event(""));

    OperationResult operationResult = effectConsumer.apply(subject);

    assertThat(operationResult.getChangedSubjects()).contains(subject);
  }

  @Test
  void shouldSendEventIfEffectExpires() {
    Subject subject = mock(Subject.class);
    ActiveEffect activeEffect = Effect.BLIND.activate();
    when(subject.getActiveEffects()).thenReturn(Set.of(activeEffect));
    when(subject.effectExpired(eq(Effect.BLIND))).thenReturn(subject);
    when(eventsFactory.effectExpiredEvent(any(), any())).thenReturn(new Event(""));

    OperationResult operationResult = effectConsumer.apply(subject);

    verify(eventsFactory).effectExpiredEvent(eq(subject), eq(Effect.BLIND));
  }
}