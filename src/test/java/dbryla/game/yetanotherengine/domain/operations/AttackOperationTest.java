package dbryla.game.yetanotherengine.domain.operations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dbryla.game.yetanotherengine.domain.Abilities;
import dbryla.game.yetanotherengine.domain.Instrument;
import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.events.EventsFactory;
import dbryla.game.yetanotherengine.domain.subjects.classes.Fighter;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Equipment;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Weapon;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AttackOperationTest {

  private final static Instrument TEST_INSTRUMENT = new Instrument(Weapon.SHORTBOW);
  private static final Abilities DEFAULT_ABILITIES = new Abilities(10, 10, 10, 10, 10, 10);
  private static final HitRoll failedHitRoll = new HitRoll(1, 0);
  private static final HitRoll successHitRoll = new HitRoll(20, 0);

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
    when(source.getAbilities()).thenReturn(DEFAULT_ABILITIES);
    Subject target = mock(Subject.class);
    when(target.of(anyInt())).thenReturn(target);
    when(fightHelper.getHitRoll(eq(source), eq(target))).thenReturn(successHitRoll);
    when(source.getEquipment()).thenReturn(new Equipment(TEST_INSTRUMENT.getWeapon()));

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
    when(source.getAbilities()).thenReturn(DEFAULT_ABILITIES);
    when(source.getEquipment()).thenReturn(new Equipment(TEST_INSTRUMENT.getWeapon()));
    Subject target = mock(Subject.class);
    when(fightHelper.getHitRoll(eq(source), eq(target))).thenReturn(failedHitRoll);

    Set<Subject> changes = operation.invoke(source, TEST_INSTRUMENT, target);

    assertThat(changes).isEmpty();
  }

  @Test
  void shouldChangeHealthPointsOfAttackedSubject() throws UnsupportedGameOperationException {
    Fighter source = mock(Fighter.class);
    when(source.getAbilities()).thenReturn(DEFAULT_ABILITIES);
    int attackDamage = 5;
    Subject target = mock(Subject.class);
    when(target.of(anyInt())).thenReturn(target);
    when(fightHelper.getHitRoll(eq(source), eq(target))).thenReturn(successHitRoll);
    when(fightHelper.getAttackDamage(anyInt(), any())).thenReturn(attackDamage);
    int initialHealth = 10;
    when(target.getCurrentHealthPoints()).thenReturn(initialHealth);
    when(source.getEquipment()).thenReturn(new Equipment(TEST_INSTRUMENT.getWeapon()));

    Set<Subject> changes = operation.invoke(source, TEST_INSTRUMENT, target);

    assertThat(changes.size()).isEqualTo(1);
    verify(target).of(initialHealth - attackDamage);
  }

  @Test
  void shouldSendSuccessAttackEventWhenTargetWasTerminated() throws UnsupportedGameOperationException {
    Fighter source = mock(Fighter.class);
    when(source.getAbilities())
        .thenReturn(DEFAULT_ABILITIES);
    int attackDamage = 10;
    Subject target = mock(Subject.class);
    when(fightHelper.getHitRoll(eq(source), eq(target))).thenReturn(successHitRoll);
    when(fightHelper.getAttackDamage(anyInt(), any())).thenReturn(attackDamage);
    Subject changedTarget = mock(Subject.class);
    when(target.of(anyInt())).thenReturn(changedTarget);
    int initialHealth = 10;
    when(target.getCurrentHealthPoints()).thenReturn(initialHealth);
    when(source.getEquipment()).thenReturn(new Equipment(TEST_INSTRUMENT.getWeapon()));

    operation.invoke(source, TEST_INSTRUMENT, target);

    verify(eventHub).send(any());
    verify(eventsFactory).successAttackEvent(any(), eq(changedTarget), any(), any());
  }

  @Test
  void shouldSendSuccessAttackEventWhenTargetWasAttacked() throws UnsupportedGameOperationException {
    Fighter source = mock(Fighter.class);
    when(source.getAbilities()).thenReturn(DEFAULT_ABILITIES);
    Subject target = mock(Subject.class);
    when(target.of(anyInt())).thenReturn(target);
    int initialHealth = 10;
    when(target.getCurrentHealthPoints()).thenReturn(initialHealth);
    when(fightHelper.getHitRoll(eq(source), eq(target))).thenReturn(successHitRoll);
    when(source.getEquipment()).thenReturn(new Equipment(TEST_INSTRUMENT.getWeapon()));

    operation.invoke(source, TEST_INSTRUMENT, target);

    verify(eventHub).send(any());
    verify(eventsFactory).successAttackEvent(any(), any(), any(), any());
  }

  @Test
  void shouldSendFailAttackEventWhenTargetWasNotAttacked() throws UnsupportedGameOperationException {
    Fighter source = mock(Fighter.class);
    when(source.getAbilities())
        .thenReturn(DEFAULT_ABILITIES);
    Subject target = mock(Subject.class);
    when(fightHelper.getHitRoll(eq(source), eq(target))).thenReturn(failedHitRoll);
    when(source.getEquipment()).thenReturn(new Equipment(TEST_INSTRUMENT.getWeapon()));

    operation.invoke(source, TEST_INSTRUMENT, target);

    verify(eventHub).send(any());
    verify(eventsFactory).failEvent(any(), any(), any(), any());
  }
}