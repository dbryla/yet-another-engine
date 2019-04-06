package dbryla.game.yetanotherengine.operations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dbryla.game.yetanotherengine.domain.Instrument;
import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.events.EventsFactory;
import dbryla.game.yetanotherengine.domain.operations.AttackOperation;
import dbryla.game.yetanotherengine.domain.operations.EffectConsumer;
import dbryla.game.yetanotherengine.domain.operations.FightHelper;
import dbryla.game.yetanotherengine.domain.operations.UnsupportedAttackException;
import dbryla.game.yetanotherengine.domain.operations.UnsupportedGameOperationException;
import dbryla.game.yetanotherengine.domain.subjects.classes.Subject;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Weapon;
import dbryla.game.yetanotherengine.domain.subjects.classes.Fighter;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AttackOperationTest {

  private final static Instrument TEST_INSTRUMENT = new Instrument(Weapon.SHORTSWORD);

  @Mock
  private EventHub eventHub;

  @Mock
  private FightHelper fightHelper;

  @Mock
  private EffectConsumer effectConsumer;

  @Mock
  private EventsFactory eventsFactory;

  @InjectMocks
  private AttackOperation operation;

  @Test
  void shouldReturnAttackedSubject() throws UnsupportedGameOperationException {
    Fighter source = mock(Fighter.class);
    when(source.getWeapon()).thenReturn(Weapon.SHORTSWORD);
    Subject target = mock(Subject.class);
    when(target.of(anyInt())).thenReturn(target);

    Set<Subject> changes = operation.invoke(source, TEST_INSTRUMENT, target);

    assertThat(changes).extracting("name").containsExactly(target.getName());
  }

  @Test
  void shouldThrowExceptionWhileTryingToAttackMoreThanOneTarget() {
    assertThrows(UnsupportedAttackException.class, () -> operation.invoke(mock(Fighter.class), TEST_INSTRUMENT, mock(Subject.class), mock(Subject.class)));
  }

  @Test
  void shouldThrowExceptionWhileTryingToAttackNoTarget() {
    assertThrows(UnsupportedAttackException.class, () -> operation.invoke(mock(Fighter.class), TEST_INSTRUMENT));
  }

  @Test
  void shouldThrowExceptionWhileTryingToInvokeOperationOnNull() {
    assertThrows(UnsupportedAttackException.class, () -> operation.invoke(null, TEST_INSTRUMENT));
  }

  @Test
  void shouldNotReturnChangesIfTargetWasNotAttacked() throws UnsupportedGameOperationException {
    Fighter source = mock(Fighter.class);
    Subject target = mock(Subject.class);
    when(fightHelper.isMiss(anyInt(), anyInt())).thenReturn(true);

    Set<Subject> changes = operation.invoke(source, TEST_INSTRUMENT, target);

    assertThat(changes).isEmpty();
  }

  @Test
  void shouldChangeHealthPointsOfAttackedSubject() throws UnsupportedGameOperationException {
    Fighter source = mock(Fighter.class);
    Weapon weapon = mock(Weapon.class);
    when(source.getWeapon()).thenReturn(weapon);
    int attackDamage = 5;
    when(fightHelper.getAttackDamage(anyInt(), anyInt())).thenReturn(attackDamage);
    Subject target = mock(Subject.class);
    when(target.of(anyInt())).thenReturn(target);
    int initialHealth = 10;
    when(target.getHealthPoints()).thenReturn(initialHealth);

    Set<Subject> changes = operation.invoke(source, TEST_INSTRUMENT, target);

    assertThat(changes.size()).isEqualTo(1);
    verify(target).of(initialHealth - attackDamage);
  }

  @Test
  void shouldSendSuccessAttackEventWhenTargetWasTerminated() throws UnsupportedGameOperationException {
    Fighter source = mock(Fighter.class);
    Weapon weapon = mock(Weapon.class);
    int attackDamage = 10;
    when(weapon.rollAttackDamage()).thenReturn(attackDamage);
    when(source.getWeapon()).thenReturn(weapon);
    Subject target = mock(Subject.class);
    Subject changedTarget = mock(Subject.class);
    when(target.of(anyInt())).thenReturn(changedTarget);
    int initialHealth = 10;
    when(target.getHealthPoints()).thenReturn(initialHealth);

    operation.invoke(source, TEST_INSTRUMENT, target);

    verify(eventHub).send(any());
    verify(eventsFactory).successAttackEvent(any(), eq(changedTarget));
  }

  @Test
  void shouldSendSuccessAttackEventWhenTargetWasAttacked() throws UnsupportedGameOperationException {
    Fighter source = mock(Fighter.class);
    Weapon weapon = mock(Weapon.class);
    int attackDamage = 5;
    when(weapon.rollAttackDamage()).thenReturn(attackDamage);
    when(source.getWeapon()).thenReturn(weapon);
    Subject target = mock(Subject.class);
    when(target.of(anyInt())).thenReturn(target);
    int initialHealth = 10;
    when(target.getHealthPoints()).thenReturn(initialHealth);

    operation.invoke(source, TEST_INSTRUMENT, target);

    verify(eventHub).send(any());
    verify(eventsFactory).successAttackEvent(any(), any());
  }

  @Test
  void shouldSendFailAttackEventWhenTargetWasNotAttacked() throws UnsupportedGameOperationException {
    Fighter source = mock(Fighter.class);
    Subject target = mock(Subject.class);
    when(fightHelper.isMiss(anyInt(), anyInt())).thenReturn(true);

    operation.invoke(source, TEST_INSTRUMENT, target);

    verify(eventHub).send(any());
    verify(eventsFactory).failEvent(any(), any());
  }
}