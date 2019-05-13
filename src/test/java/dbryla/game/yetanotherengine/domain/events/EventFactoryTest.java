package dbryla.game.yetanotherengine.domain.events;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dbryla.game.yetanotherengine.domain.effects.Effect;
import dbryla.game.yetanotherengine.domain.equipment.Weapon;
import dbryla.game.yetanotherengine.domain.operations.HitResult;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.HealthState;
import dbryla.game.yetanotherengine.domain.subject.State;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventFactoryTest {

  private EventFactory eventFactory = new EventFactory();

  @Test
  void shouldReturnSuccessAttackEventMessage() {
    Subject attacker = mock(Subject.class);
    when(attacker.getName()).thenReturn("attacker");
    State target = mock(State.class);
    when(target.getSubjectName()).thenReturn("target");
    when(target.getHealthState()).thenReturn(HealthState.HEAVILY_WOUNDED);

    Event event = eventFactory.successAttackEvent(attacker, target, Weapon.GREATSWORD, HitResult.HIT);

    assertThat(event.toString()).isEqualTo("attacker hits target with greatsword. target is heavily wounded.");
  }

  @Test
  void shouldReturnSuccessSpellCastEventMessage() {
    Subject attacker = mock(Subject.class);
    when(attacker.getName()).thenReturn("attacker");
    State target = mock(State.class);
    when(target.getSubjectName()).thenReturn("target");
    when(target.getHealthState()).thenReturn(HealthState.HEAVILY_WOUNDED);

    Event event = eventFactory.successSpellCastEvent(attacker, target, Spell.FIRE_BOLT);

    assertThat(event.toString()).isEqualTo("attacker casts fire bolt and hits target. target is heavily wounded.");
  }

  @Test
  void shouldReturnFailEventMessage() {
    Subject attacker = mock(Subject.class);
    when(attacker.getName()).thenReturn("attacker");
    Subject target = mock(Subject.class);
    when(target.getName()).thenReturn("target");

    Event event = eventFactory.failEvent(attacker, target, "weapons", HitResult.MISS);

    assertThat(event.toString()).isEqualTo("attacker misses attack on target with weapons.");
  }

  @Test
  void shouldEffectExpiredEventMessage() {
    Subject attacker = mock(Subject.class);
    when(attacker.getName()).thenReturn("attacker");

    Event event = eventFactory.effectExpiredEvent(attacker, Effect.BLINDED);

    assertThat(event.toString()).isEqualTo("attacker is no longer blinded.");
  }

  @Test
  void shouldReturnEquipsWeaponEvent() {
    State attacker = mock(State.class);
    when(attacker.getSubjectName()).thenReturn("attacker");
    when(attacker.getEquippedWeapon()).thenReturn(Weapon.SHORTSWORD);

    Event event = eventFactory.equipWeaponEvent(attacker);

    assertThat(event.toString()).isEqualTo("attacker equips shortsword.");
  }
}