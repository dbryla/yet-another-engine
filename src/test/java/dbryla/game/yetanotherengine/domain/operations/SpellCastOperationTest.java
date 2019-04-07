package dbryla.game.yetanotherengine.domain.operations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import dbryla.game.yetanotherengine.domain.Instrument;
import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.events.EventsFactory;
import dbryla.game.yetanotherengine.domain.spells.Effect;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subjects.classes.Subject;
import dbryla.game.yetanotherengine.domain.subjects.classes.Wizard;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SpellCastOperationTest {

  @InjectMocks
  private SpellCastOperation spellCastOperation;

  @Mock
  private EventHub eventHub;

  @Mock
  private FightHelper fightHelper;

  @Mock
  private EffectConsumer effectConsumer;

  @Mock
  private EventsFactory eventsFactory;

  @Mock
  private Wizard wizard;

  @Mock
  private Subject target;

  @Test
  void shouldInvokeDmgSpell() throws UnsupportedGameOperationException {
    Instrument instrument = new Instrument(Spell.FIRE_BOLT);
    Subject changedTarget = mock(Subject.class);
    when(target.of(anyInt())).thenReturn(changedTarget);

    Set<Subject> changes = spellCastOperation.invoke(wizard, instrument, target);

    assertThat(changes).contains(changedTarget);
  }

  @Test
  void shouldInvokeEffectSpell() throws UnsupportedGameOperationException {
    Instrument instrument = new Instrument(Spell.COLOR_SPRAY);
    Subject changedTarget = mock(Subject.class);
    when(target.of(Effect.BLIND)).thenReturn(changedTarget);

    Set<Subject> changes = spellCastOperation.invoke(wizard, instrument, target);

    assertThat(changes).contains(changedTarget);
  }

  @Test
  void shouldThrowExceptionIfSpellDoesNotSupportSoManyTargets() {
    Instrument instrument = new Instrument(Spell.FIRE_BOLT);

    assertThrows(UnsupportedSpellCastException.class, () -> spellCastOperation.invoke(wizard, instrument, target, target));
  }

  @Test
  void shouldGetHitRollIfSpellIsTypeOfAttack() throws UnsupportedGameOperationException {
    Instrument instrument = new Instrument(Spell.FIRE_BOLT);
    Subject changedTarget = mock(Subject.class);
    when(target.of(anyInt())).thenReturn(changedTarget);

    spellCastOperation.invoke(wizard, instrument, target);

    verify(fightHelper).getHitRoll(eq(wizard), eq(target));
  }

  @Test
  void shouldNotGetHitRollIfSpellIsTypeOfIrresistible() throws UnsupportedGameOperationException {
    Instrument instrument = new Instrument(Spell.COLOR_SPRAY);
    Subject changedTarget = mock(Subject.class);
    when(target.of(eq(Effect.BLIND))).thenReturn(changedTarget);

    spellCastOperation.invoke(wizard, instrument, target);

    verifyZeroInteractions(fightHelper);
  }

  @Test
  void shouldInvokeEffectConsumer() throws UnsupportedGameOperationException {
    Instrument instrument = new Instrument(Spell.FIRE_BOLT);
    Subject changedTarget = mock(Subject.class);
    when(target.of(anyInt())).thenReturn(changedTarget);

    spellCastOperation.invoke(wizard, instrument, target);

    verify(effectConsumer).apply(eq(wizard));
  }

  @Test
  void shouldSendSuccessEventOnSuccessfulSpellCast() throws UnsupportedGameOperationException {
    Instrument instrument = new Instrument(Spell.COLOR_SPRAY);
    Subject changedTarget = mock(Subject.class);
    when(target.of(eq(Effect.BLIND))).thenReturn(changedTarget);

    spellCastOperation.invoke(wizard, instrument, target);

    verify(eventsFactory).successSpellCastEvent(any(), eq(changedTarget), eq(Spell.COLOR_SPRAY));
    verify(eventHub).send(any());
  }

  @Test
  void shouldSendFailEventOnUnsuccessfulSpellCast() throws UnsupportedGameOperationException {
    Instrument instrument = new Instrument(Spell.FIRE_BOLT);
    when(fightHelper.isMiss(anyInt(), anyInt(), anyInt())).thenReturn(true);

    spellCastOperation.invoke(wizard, instrument, target);

    verify(eventsFactory).failEvent(any(), any());
    verify(eventHub).send(any());
  }
}