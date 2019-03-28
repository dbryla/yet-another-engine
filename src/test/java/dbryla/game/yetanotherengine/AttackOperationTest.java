package dbryla.game.yetanotherengine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AttackOperationTest {

  @Mock
  private EventLog eventLog;

  @InjectMocks
  private AttackOperation operation;

  @Test
  void shouldReturnAttackedSubject() throws UnsupportedGameOperationException {
    Fighter source = mock(Fighter.class);
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
    when(source.calculateHitRoll()).thenReturn(0);
    when(target.getArmorClass()).thenReturn(10);

    Set<Subject> changes = operation.invoke(source, target);

    assertThat(changes).isEmpty();
  }

  @Test
  void shouldChangeHealthPointsOfAttackedSubject() throws UnsupportedGameOperationException {
    Fighter source = mock(Fighter.class);
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
    int attackDamage = 10;
    when(source.calculateAttackDamage()).thenReturn(attackDamage);
    Subject target = mock(Subject.class);
    when(target.of(anyInt())).thenReturn(target);
    int initialHealth = 10;
    when(target.getHealthPoints()).thenReturn(initialHealth);

    operation.invoke(source, target);

    verify(eventLog).send(eq(AttackEvent.success(source.getName(), target.getName(), true)));
  }

  @Test
  void shouldSendSuccessAttackEventWhenTargetWasAttacked() throws UnsupportedGameOperationException {
    Fighter source = mock(Fighter.class);
    int attackDamage = 5;
    when(source.calculateAttackDamage()).thenReturn(attackDamage);
    Subject target = mock(Subject.class);
    when(target.of(anyInt())).thenReturn(target);
    int initialHealth = 10;
    when(target.getHealthPoints()).thenReturn(initialHealth);

    operation.invoke(source, target);

    verify(eventLog).send(eq(AttackEvent.success(source.getName(), target.getName(), false)));
  }

  @Test
  void shouldSendFailAttackEventWhenTargetWasNotAttacked() throws UnsupportedGameOperationException {
    Fighter source = mock(Fighter.class);
    Subject target = mock(Subject.class);
    when(source.calculateHitRoll()).thenReturn(0);
    when(target.getArmorClass()).thenReturn(10);

    operation.invoke(source, target);

    verify(eventLog).send(eq(AttackEvent.fail(source.getName(), target.getName())));
  }
}