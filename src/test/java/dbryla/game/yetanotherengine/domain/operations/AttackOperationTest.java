package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.dice.DiceRollService;
import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.events.EventFactory;
import dbryla.game.yetanotherengine.domain.subject.Abilities;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttackOperationTest {

  private final static ActionData TEST_ACTION_DATA = new ActionData(Weapon.SHORTBOW);
  private static final Abilities DEFAULT_ABILITIES = new Abilities(10, 10, 10, 10, 10, 10);
  private static final HitRoll failedHitRoll = new HitRoll(1, 0);
  private static final HitRoll successHitRoll = new HitRoll(20, 0);

  @Mock
  private EventHub eventHub;

  @Mock
  private FightHelper fightHelper;

  @Mock
  private EventFactory eventFactory;

  @Mock
  private DiceRollService diceRollService;

  @InjectMocks
  private AttackOperation operation;

  @Test
  void shouldReturnAttackedSubject() throws UnsupportedGameOperationException {
    Subject source = givenSourceWithEquippedWeapon();
    Subject target = mock(Subject.class);
    when(fightHelper.getHitRoll(eq(source), eq(target))).thenReturn(successHitRoll);
    when(fightHelper.dealDamage(eq(target), anyInt(), any())).thenReturn(Optional.of(target));

    OperationResult operationResult = operation.invoke(source, TEST_ACTION_DATA, target);

    assertThat(operationResult.getChangedSubjects()).extracting("name").containsExactly(target.getName());
  }

  private Subject givenSourceWithEquippedWeapon() {
    Subject source = mock(Subject.class);
    when(source.getAbilities()).thenReturn(DEFAULT_ABILITIES);
    when(source.getEquippedWeapon()).thenReturn(TEST_ACTION_DATA.getWeapon());
    return source;
  }

  @Test
  void shouldThrowExceptionWhileTryingToAttackMoreThanOneTarget() {
    assertThrows(UnsupportedAttackException.class, () -> operation.invoke(mock(Subject.class), TEST_ACTION_DATA, mock(Subject.class), mock(Subject.class)));
  }

  @Test
  void shouldThrowExceptionWhileTryingToAttackNoTarget() {
    assertThrows(UnsupportedAttackException.class, () -> operation.invoke(mock(Subject.class), TEST_ACTION_DATA));
  }

  @Test
  void shouldThrowExceptionWhileTryingToInvokeOperationOnNull() {
    assertThrows(UnsupportedAttackException.class, () -> operation.invoke(null, TEST_ACTION_DATA));
  }

  @Test
  void shouldNotReturnChangesIfTargetWasNotAttacked() throws UnsupportedGameOperationException {
    Subject source = givenSourceWithEquippedWeapon();
    Subject target = mock(Subject.class);
    when(fightHelper.getHitRoll(eq(source), eq(target))).thenReturn(failedHitRoll);

    OperationResult operationResult = operation.invoke(source, TEST_ACTION_DATA, target);

    assertThat(operationResult.getChangedSubjects()).isEmpty();
  }

  @Test
  void shouldChangeHealthPointsOfAttackedSubject() throws UnsupportedGameOperationException {
    Subject source = givenSourceWithEquippedWeapon();
    int attackDamage = 5;
    Subject target = mock(Subject.class);
    when(fightHelper.getHitRoll(eq(source), eq(target))).thenReturn(successHitRoll);
    when(fightHelper.getAttackDamage(any(), any(), any())).thenReturn(attackDamage);
    when(fightHelper.dealDamage(eq(target), anyInt(), any())).thenReturn(Optional.of(target));

    OperationResult operationResult = operation.invoke(source, TEST_ACTION_DATA, target);

    assertThat(operationResult.getChangedSubjects().size()).isEqualTo(1);
    verify(fightHelper).dealDamage(eq(target), eq(attackDamage), any());
  }

  @Test
  void shouldCreateSuccessAttackEventWhenTargetWasTerminated() throws UnsupportedGameOperationException {
    Subject source = givenSourceWithEquippedWeapon();
    int attackDamage = 10;
    Subject target = mock(Subject.class);
    when(fightHelper.getHitRoll(eq(source), eq(target))).thenReturn(successHitRoll);
    when(fightHelper.getAttackDamage(any(), any(), any())).thenReturn(attackDamage);
    when(fightHelper.dealDamage(eq(target), anyInt(), any())).thenReturn(Optional.of(target));

    operation.invoke(source, TEST_ACTION_DATA, target);

    verify(eventFactory).successAttackEvent(any(), any(), any(), any());
  }

  @Test
  void shouldCreateSuccessAttackEventWhenTargetWasAttacked() throws UnsupportedGameOperationException {
    Subject source = givenSourceWithEquippedWeapon();
    Subject target = mock(Subject.class);
    when(fightHelper.getHitRoll(eq(source), eq(target))).thenReturn(successHitRoll);
    when(fightHelper.dealDamage(eq(target), anyInt(), any())).thenReturn(Optional.of(target));

    operation.invoke(source, TEST_ACTION_DATA, target);

    verify(eventFactory).successAttackEvent(any(), any(), any(), any());
  }

  @Test
  void shouldCreateFailAttackEventWhenTargetWasNotAttacked() throws UnsupportedGameOperationException {
    Subject source = givenSourceWithEquippedWeapon();
    Subject target = mock(Subject.class);
    when(fightHelper.getHitRoll(eq(source), eq(target))).thenReturn(failedHitRoll);

    operation.invoke(source, TEST_ACTION_DATA, target);

    verify(eventFactory).failEvent(any(), any(), any(), any());
  }

  @Test
  void shouldNotChangeSourceIfAttacksWithEquippedWeapon() throws UnsupportedGameOperationException {
    Subject source = givenSourceWithEquippedWeapon();
    Subject target = mock(Subject.class);
    when(fightHelper.getHitRoll(eq(source), eq(target))).thenReturn(successHitRoll);

    operation.invoke(source, TEST_ACTION_DATA, target);

    verify(source, times(0)).of(eq(TEST_ACTION_DATA.getWeapon()));
    verify(eventFactory, times(0)).equipWeaponEvent(any());
  }

  @Test
  void shouldChangeSourceEquippedWeaponIfAttacksWithDifferentOne() throws UnsupportedGameOperationException {
    Subject source = mock(Subject.class);
    when(source.getAbilities()).thenReturn(DEFAULT_ABILITIES);
    when(source.getEquippedWeapon()).thenReturn(Weapon.FISTS);
    Subject target = mock(Subject.class);
    when(fightHelper.getHitRoll(eq(source), eq(target))).thenReturn(successHitRoll);

    operation.invoke(source, TEST_ACTION_DATA, target);

    verify(source).of(eq(TEST_ACTION_DATA.getWeapon()));
    verify(eventFactory).equipWeaponEvent(any());
  }
}