package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.dice.DiceRollService;
import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.events.EventsFactory;
import dbryla.game.yetanotherengine.domain.subject.Abilities;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.equipment.Equipment;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
  private EventsFactory eventsFactory;

  @Mock
  private DiceRollService diceRollService;

  @InjectMocks
  private AttackOperation operation;

  @Test
  void shouldReturnAttackedSubject() throws UnsupportedGameOperationException {
    Subject source = mock(Subject.class);
    when(source.getAbilities()).thenReturn(DEFAULT_ABILITIES);
    Subject target = mock(Subject.class);
    when(fightHelper.getHitRoll(eq(source), eq(target))).thenReturn(successHitRoll);
    when(source.getEquipment()).thenReturn(new Equipment(TEST_ACTION_DATA.getWeapon()));
    when(fightHelper.dealDamage(eq(target), anyInt())).thenReturn(target);

    OperationResult operationResult = operation.invoke(source, TEST_ACTION_DATA, target);

    assertThat(operationResult.getChangedSubjects()).extracting("name").containsExactly(target.getName());
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
    Subject source = mock(Subject.class);
    when(source.getAbilities()).thenReturn(DEFAULT_ABILITIES);
    when(source.getEquipment()).thenReturn(new Equipment(TEST_ACTION_DATA.getWeapon()));
    Subject target = mock(Subject.class);
    when(fightHelper.getHitRoll(eq(source), eq(target))).thenReturn(failedHitRoll);

    OperationResult operationResult = operation.invoke(source, TEST_ACTION_DATA, target);

    assertThat(operationResult.getChangedSubjects()).isEmpty();
  }

  @Test
  void shouldChangeHealthPointsOfAttackedSubject() throws UnsupportedGameOperationException {
    Subject source = mock(Subject.class);
    when(source.getAbilities()).thenReturn(DEFAULT_ABILITIES);
    int attackDamage = 5;
    Subject target = mock(Subject.class);
    when(fightHelper.getHitRoll(eq(source), eq(target))).thenReturn(successHitRoll);
    when(fightHelper.getAttackDamage(anyInt(), any())).thenReturn(attackDamage);
    when(source.getEquipment()).thenReturn(new Equipment(TEST_ACTION_DATA.getWeapon()));

    OperationResult operationResult = operation.invoke(source, TEST_ACTION_DATA, target);

    assertThat(operationResult.getChangedSubjects().size()).isEqualTo(1);
    verify(fightHelper).dealDamage(target, attackDamage);
  }

  @Test
  void shouldCreateSuccessAttackEventWhenTargetWasTerminated() throws UnsupportedGameOperationException {
    Subject source = mock(Subject.class);
    when(source.getAbilities())
        .thenReturn(DEFAULT_ABILITIES);
    int attackDamage = 10;
    Subject target = mock(Subject.class);
    when(fightHelper.getHitRoll(eq(source), eq(target))).thenReturn(successHitRoll);
    when(fightHelper.getAttackDamage(anyInt(), any())).thenReturn(attackDamage);
    when(source.getEquipment()).thenReturn(new Equipment(TEST_ACTION_DATA.getWeapon()));

    operation.invoke(source, TEST_ACTION_DATA, target);

    verify(eventsFactory).successAttackEvent(any(), any(), any(), any());
  }

  @Test
  void shouldCreateSuccessAttackEventWhenTargetWasAttacked() throws UnsupportedGameOperationException {
    Subject source = mock(Subject.class);
    when(source.getAbilities()).thenReturn(DEFAULT_ABILITIES);
    Subject target = mock(Subject.class);
    when(fightHelper.getHitRoll(eq(source), eq(target))).thenReturn(successHitRoll);
    when(source.getEquipment()).thenReturn(new Equipment(TEST_ACTION_DATA.getWeapon()));

    operation.invoke(source, TEST_ACTION_DATA, target);

    verify(eventsFactory).successAttackEvent(any(), any(), any(), any());
  }

  @Test
  void shouldCreateFailAttackEventWhenTargetWasNotAttacked() throws UnsupportedGameOperationException {
    Subject source = mock(Subject.class);
    when(source.getAbilities())
        .thenReturn(DEFAULT_ABILITIES);
    Subject target = mock(Subject.class);
    when(fightHelper.getHitRoll(eq(source), eq(target))).thenReturn(failedHitRoll);
    when(source.getEquipment()).thenReturn(new Equipment(TEST_ACTION_DATA.getWeapon()));

    operation.invoke(source, TEST_ACTION_DATA, target);

    verify(eventsFactory).failEvent(any(), any(), any(), any());
  }
}