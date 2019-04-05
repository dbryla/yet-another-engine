package dbryla.game.yetanotherengine.domain.operations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.events.EventsFactory;
import dbryla.game.yetanotherengine.domain.spells.Effect;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import java.util.Optional;
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
  private EventHub eventHub;

  @Mock
  private EventsFactory eventsFactory;

  @Test
  void shouldReturnEmptyOptionalIfNoEffectIsActive() {
    Subject subject = mock(Subject.class);
    when(subject.getActiveEffect()).thenReturn(Optional.empty());

    Optional<Subject> changes = effectConsumer.apply(subject);

    assertThat(changes).isEmpty();
  }


  @Test
  void shouldReturnEmptyOptionalIfEffectIsStillActive() {
    Subject subject = mock(Subject.class);
    when(subject.getActiveEffect()).thenReturn(Optional.of(Effect.BLIND));
    when(subject.getActiveEffectDurationInTurns()).thenReturn(1);

    Optional<Subject> changes = effectConsumer.apply(subject);

    assertThat(changes).isEmpty();
  }

  @Test
  void shouldReturnEmptyOptionalIfEffectExpires() {
    Subject subject = mock(Subject.class);
    when(subject.getActiveEffect()).thenReturn(Optional.of(Effect.BLIND));
    when(subject.getActiveEffectDurationInTurns()).thenReturn(0);
    when(subject.effectExpired()).thenReturn(subject);

    Optional<Subject> changes = effectConsumer.apply(subject);

    assertThat(changes).isPresent();
    assertThat(changes).contains(subject);
  }

  @Test
  void shouldSendEventIfEffectExpires() {
    Subject subject = mock(Subject.class);
    when(subject.getActiveEffect()).thenReturn(Optional.of(Effect.BLIND));
    when(subject.getActiveEffectDurationInTurns()).thenReturn(0);
    when(subject.effectExpired()).thenReturn(subject);

    Optional<Subject> changes = effectConsumer.apply(subject);

    verify(eventHub).send(any());
    verify(eventsFactory).effectExpiredEvent(eq(subject));
  }
}