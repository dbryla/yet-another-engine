package dbryla.game.yetanotherengine.domain.events;

import dbryla.game.yetanotherengine.domain.spells.Effect;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.domain.subjects.classes.Mage;
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
    Mage attacker = mock(Mage.class);
    when(attacker.getName()).thenReturn("attacker");
    when(attacker.getSpell()).thenReturn(Spell.FIRE_BOLT);
    Subject target = mock(Subject.class);
    when(target.getName()).thenReturn("target");

    Event event = eventsFactory.successSpellCastEvent(attacker, target);

    assertThat(event.toString()).isEqualTo("attacker hits target with fire bolt.");
  }

  @Test
  void shouldReturnFailEventMessage() {
    Mage attacker = mock(Mage.class);
    when(attacker.getName()).thenReturn("attacker");
    Subject target = mock(Subject.class);
    when(target.getName()).thenReturn("target");

    Event event = eventsFactory.failEvent(attacker, target);

    assertThat(event.toString()).isEqualTo("attacker misses attack on target.");
  }

  @Test
  void shouldEffectExpiredEventMessage() {
    Mage attacker = mock(Mage.class);
    when(attacker.getName()).thenReturn("attacker");
    when(attacker.getActiveEffect()).thenReturn(Optional.of(Effect.BLIND));

    Event event = eventsFactory.effectExpiredEvent(attacker);

    assertThat(event.toString()).isEqualTo("attacker is no longer blinded.");
  }
}