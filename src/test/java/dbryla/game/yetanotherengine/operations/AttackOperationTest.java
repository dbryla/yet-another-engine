package dbryla.game.yetanotherengine.operations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.events.EventsFactory;
import dbryla.game.yetanotherengine.domain.operations.AttackOperation;
import dbryla.game.yetanotherengine.domain.operations.EffectConsumer;
import dbryla.game.yetanotherengine.domain.operations.HitRollSupplier;
import dbryla.game.yetanotherengine.domain.operations.UnsupportedAttackException;
import dbryla.game.yetanotherengine.domain.operations.UnsupportedGameOperationException;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.domain.subjects.Weapon;
import dbryla.game.yetanotherengine.domain.subjects.classes.Fighter;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AttackOperationTest {

  @Mock
  private EventHub eventHub;

  @Mock
  private HitRollSupplier hitRollSupplier;

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

    Set<Subject> changes = operation.invoke(source, target);

    assertThat(changes).extracting("name").containsExactly(target.getName());
  }

  @Test
  void shouldThrowExceptionWhileTryingToAttackMoreThanOneTarget() {
    assertThrows(UnsupportedAttackException.class, () -> operation.invoke(mock(Fighter.class), mock(Subject.class), mock(Subject.class)));
  }

  @Test
  void shouldThrowExceptionWhileTryingToAttackNoTarget() {
    assertThrows(UnsupportedAttackException.class, () -> operation.invoke(mock(Fighter.class)));
  }

  @Test
  void shouldThrowExceptionWhileTryingToInvokeOperationOnNull() {
    assertThrows(UnsupportedAttackException.class, () -> operation.invoke(null));
  }

  @Test
  void shouldNotReturnChangesIfTargetWasNotAttacked() throws UnsupportedGameOperationException {
    Fighter source = mock(Fighter.class);
    Subject target = mock(Subject.class);
    when(hitRollSupplier.get(eq(source), eq(target))).thenReturn(0);
    when(target.getArmorClass()).thenReturn(10);

    Set<Subject> changes = operation.invoke(source, target);

    assertThat(changes).isEmpty();
  }

  @Test
  void shouldChangeHealthPointsOfAttackedSubject() throws UnsupportedGameOperationException {
    Fighter source = mock(Fighter.class);
    when(source.getWeapon()).thenReturn(Weapon.SHORTSWORD);
    int attackDamage = 5;
    when(source.calculateAttackDamage()).thenReturn(attackDamage);
    Subject target = mock(Subject.class);
    when(target.of(anyInt())).thenReturn(target);
    int initialHealth = 10;
    when(target.getHealthPoints()).thenReturn(initialHealth);

    Set<Subject> changes = operation.invoke(source, target);

    assertThat(changes.size()).isEqualTo(1);
    verify(target).of(initialHealth - attackDamage);
  }

  @Test
  void shouldSendSuccessAttackEventWhenTargetWasTerminated() throws UnsupportedGameOperationException {
    Fighter source = mock(Fighter.class);
    when(source.getWeapon()).thenReturn(Weapon.SHORTSWORD);
    int attackDamage = 10;
    when(source.calculateAttackDamage()).thenReturn(attackDamage);
    Subject target = mock(Subject.class);
    when(target.of(anyInt())).thenReturn(target);
    int initialHealth = 10;
    when(target.getHealthPoints()).thenReturn(initialHealth);

    operation.invoke(source, target);

    verify(eventHub).send(any());
    verify(eventsFactory).successAttackEvent(any(), any(), anyBoolean(), any());
  }

  @Test
  void shouldSendSuccessAttackEventWhenTargetWasAttacked() throws UnsupportedGameOperationException {
    Fighter source = mock(Fighter.class);
    when(source.getWeapon()).thenReturn(Weapon.SHORTSWORD);
    int attackDamage = 5;
    when(source.calculateAttackDamage()).thenReturn(attackDamage);
    Subject target = mock(Subject.class);
    when(target.of(anyInt())).thenReturn(target);
    int initialHealth = 10;
    when(target.getHealthPoints()).thenReturn(initialHealth);

    operation.invoke(source, target);

    verify(eventHub).send(any());
    verify(eventsFactory).successAttackEvent(any(), any(), anyBoolean(), any());
  }

  @Test
  void shouldSendFailAttackEventWhenTargetWasNotAttacked() throws UnsupportedGameOperationException {
    Fighter source = mock(Fighter.class);
    Subject target = mock(Subject.class);
    when(hitRollSupplier.get(eq(source), eq(target))).thenReturn(0);
    when(target.getArmorClass()).thenReturn(10);

    operation.invoke(source, target);


    verify(eventHub).send(any());
    verify(eventsFactory).failEvent(any(), any());
  }
}