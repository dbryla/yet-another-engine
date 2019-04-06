package dbryla.game.yetanotherengine.domain.events;

import dbryla.game.yetanotherengine.domain.spells.Effect;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subjects.classes.Subject;
import dbryla.game.yetanotherengine.domain.subjects.classes.Wizard;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventsFactoryTest {

  private EventsFactory eventsFactory = new EventsFactory();

  @Test
  void shouldReturnSuccessAttackEventMessage() {
    Subject attacker = mock(Subject.class);
    when(attacker.getName()).thenReturn("attacker");
    Subject target = mock(Subject.class);
    when(target.getName()).thenReturn("target");

    Event event = eventsFactory.successAttackEvent(attacker, target);

    assertThat(event.toString()).isEqualTo("attacker hits target with fists.");
  }

  @Test
  void shouldReturnSuccessSpellCastEventMessage() {
    Wizard attacker = mock(Wizard.class);
    when(attacker.getName()).thenReturn("attacker");
    when(attacker.getSpell()).thenReturn(Spell.FIRE_BOLT);
    Subject target = mock(Subject.class);
    when(target.getName()).thenReturn("target");

    Event event = eventsFactory.successSpellCastEvent(attacker, target);

    assertThat(event.toString()).isEqualTo("attacker hits target with fire bolt.");
  }

  @Test
  void shouldReturnFailEventMessage() {
    Wizard attacker = mock(Wizard.class);
    when(attacker.getName()).thenReturn("attacker");
    Subject target = mock(Subject.class);
    when(target.getName()).thenReturn("target");

    Event event = eventsFactory.failEvent(attacker, target);

    assertThat(event.toString()).isEqualTo("attacker misses attack on target.");
  }

  @Test
  void shouldEffectExpiredEventMessage() {
    Wizard attacker = mock(Wizard.class);
    when(attacker.getName()).thenReturn("attacker");
    when(attacker.getActiveEffect()).thenReturn(Optional.of(Effect.BLIND.activate()));

    Event event = eventsFactory.effectExpiredEvent(attacker);

    assertThat(event.toString()).isEqualTo("attacker is no longer blinded.");
  }
}