package dbryla.game.yetanotherengine.domain.events;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dbryla.game.yetanotherengine.domain.operations.HitResult;
import dbryla.game.yetanotherengine.domain.spells.Effect;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subjects.classes.State;
import dbryla.game.yetanotherengine.domain.subjects.classes.Subject;
import dbryla.game.yetanotherengine.domain.subjects.classes.Wizard;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Weapon;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventsFactoryTest {

  private EventsFactory eventsFactory = new EventsFactory();

  @Test
  void shouldReturnSuccessAttackEventMessage() {
    Subject attacker = mock(Subject.class);
    when(attacker.getName()).thenReturn("attacker");
    Subject target = mock(Subject.class);
    when(target.getName()).thenReturn("target");
    when(target.getSubjectState()).thenReturn(State.HEAVILY_WOUNDED);

    Event event = eventsFactory.successAttackEvent(attacker, target, Weapon.GREATSWORD, HitResult.HIT);

    assertThat(event.toString()).isEqualTo("attacker hits target with greatsword. target is heavily wounded.");
  }

  @Test
  void shouldReturnSuccessSpellCastEventMessage() {
    Wizard attacker = mock(Wizard.class);
    when(attacker.getName()).thenReturn("attacker");
    Subject target = mock(Subject.class);
    when(target.getName()).thenReturn("target");
    when(target.getSubjectState()).thenReturn(State.HEAVILY_WOUNDED);

    Event event = eventsFactory.successSpellCastEvent(attacker, target, Spell.FIRE_BOLT);

    assertThat(event.toString()).isEqualTo("attacker casts fire bolt and hits target. target is heavily wounded.");
  }

  @Test
  void shouldReturnFailEventMessage() {
    Wizard attacker = mock(Wizard.class);
    when(attacker.getName()).thenReturn("attacker");
    Subject target = mock(Subject.class);
    when(target.getName()).thenReturn("target");

    Event event = eventsFactory.failEvent(attacker, target, "weapon", HitResult.MISS);

    assertThat(event.toString()).isEqualTo("attacker misses attack on target with weapon.");
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