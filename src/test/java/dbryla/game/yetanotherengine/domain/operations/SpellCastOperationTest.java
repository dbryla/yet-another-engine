package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.dice.DiceRollService;
import dbryla.game.yetanotherengine.domain.effects.Effect;
import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.events.EventsFactory;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.Subject;
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
class SpellCastOperationTest {

  private static final HitRoll failedHitRoll = new HitRoll(1, 0);
  private static final HitRoll successHitRoll = new HitRoll(20, 0);

  @InjectMocks
  private SpellCastOperation spellCastOperation;

  @Mock
  private FightHelper fightHelper;

  @Mock
  private EventsFactory eventsFactory;

  @Mock
  private Subject source;

  @Mock
  private Subject target;

  @Mock
  private DiceRollService diceRollService;

  @Test
  void shouldInvokeDmgSpell() throws UnsupportedGameOperationException {
    ActionData actionData = new ActionData(Spell.FIRE_BOLT);
    when(fightHelper.getHitRoll(eq(source), eq(target))).thenReturn(successHitRoll);
    when(fightHelper.dealDamage(eq(target), anyInt())).thenReturn(target);

    OperationResult operationResult = spellCastOperation.invoke(source, actionData, target);

    assertThat(operationResult.getChangedSubjects()).contains(target);
  }

  @Test
  void shouldInvokeEffectSpell() throws UnsupportedGameOperationException {
    ActionData actionData = new ActionData(Spell.COLOR_SPRAY);
    Subject changedTarget = mock(Subject.class);
    when(target.of(Effect.BLIND)).thenReturn(changedTarget);

    OperationResult operationResult = spellCastOperation.invoke(source, actionData, target);

    assertThat(operationResult.getChangedSubjects()).contains(changedTarget);
  }

  @Test
  void shouldThrowExceptionIfSpellDoesNotSupportSoManyTargets() {
    ActionData actionData = new ActionData(Spell.FIRE_BOLT);

    assertThrows(UnsupportedSpellCastException.class, () -> spellCastOperation.invoke(source, actionData, target, target));
  }

  @Test
  void shouldGetHitRollIfSpellIsTypeOfAttack() throws UnsupportedGameOperationException {
    ActionData actionData = new ActionData(Spell.FIRE_BOLT);
    when(fightHelper.getHitRoll(eq(source), eq(target))).thenReturn(successHitRoll);

    spellCastOperation.invoke(source, actionData, target);

    verify(fightHelper).getHitRoll(eq(source), eq(target));
  }

  @Test
  void shouldNotGetHitRollIfSpellIsTypeOfIrresistible() throws UnsupportedGameOperationException {
    ActionData actionData = new ActionData(Spell.COLOR_SPRAY);
    Subject changedTarget = mock(Subject.class);
    when(target.of(eq(Effect.BLIND))).thenReturn(changedTarget);

    spellCastOperation.invoke(source, actionData, target);

    verifyZeroInteractions(fightHelper);
  }

  @Test
  void shouldCreateEventOnSuccessfulSpellCast() throws UnsupportedGameOperationException {
    ActionData actionData = new ActionData(Spell.COLOR_SPRAY);
    Subject changedTarget = mock(Subject.class);
    when(target.of(eq(Effect.BLIND))).thenReturn(changedTarget);

    spellCastOperation.invoke(source, actionData, target);

    verify(eventsFactory).successSpellCastEvent(any(), eq(changedTarget), eq(Spell.COLOR_SPRAY));
  }

  @Test
  void shouldCreateEventOnUnsuccessfulSpellCast() throws UnsupportedGameOperationException {
    ActionData actionData = new ActionData(Spell.FIRE_BOLT);
    when(fightHelper.getHitRoll(eq(source), eq(target))).thenReturn(failedHitRoll);

    spellCastOperation.invoke(source, actionData, target);

    verify(eventsFactory).failEvent(any(), any(), any(), any());
  }

  @Test
  void shouldHealTargetToMaxHealthOnly() throws UnsupportedGameOperationException {
    when(diceRollService.of(eq(4))).thenReturn(4);
    when(target.getCurrentHealthPoints()).thenReturn(7);
    when(target.getMaxHealthPoints()).thenReturn(10);

    spellCastOperation.invoke(source, new ActionData(Spell.HEALING_WORD), target);

    verify(target).of(eq(target.getMaxHealthPoints()));
  }
}