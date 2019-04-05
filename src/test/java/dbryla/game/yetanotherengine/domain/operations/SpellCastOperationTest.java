package dbryla.game.yetanotherengine.domain.operations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.events.EventsFactory;
import dbryla.game.yetanotherengine.domain.spells.Effect;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.domain.subjects.classes.Mage;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SpellCastOperationTest {

  private static final String SOURCE = "source";
  private static final String TARGET = "target";

  @InjectMocks
  private SpellCastOperation spellCastOperation;

  @Mock
  private EventHub eventHub;

  @Mock
  private HitRollSupplier hitRollSupplier;

  @Mock
  private EffectConsumer effectConsumer;

  @Mock
  private EventsFactory eventsFactory;

  @Mock
  private Mage mage;

  @Mock
  private Subject target;

  @Test
  void shouldInvokeDmgSpell() throws UnsupportedGameOperationException {
    when(mage.getName()).thenReturn(SOURCE);
    when(mage.getSpell()).thenReturn(Spell.FIRE_BOLT);
    Subject changedTarget = mock(Subject.class);
    when(target.of(anyInt())).thenReturn(changedTarget);

    Set<Subject> changes = spellCastOperation.invoke(mage, target);

    assertThat(changes).contains(changedTarget);
  }

  @Test
  void shouldInvokeEffectSpell() throws UnsupportedGameOperationException {
    when(mage.getName()).thenReturn(SOURCE);
    when(mage.getSpell()).thenReturn(Spell.COLOR_SPRAY);
    Subject changedTarget = mock(Subject.class);
    when(target.of(Effect.BLIND)).thenReturn(changedTarget);

    Set<Subject> changes = spellCastOperation.invoke(mage, target);

    assertThat(changes).contains(changedTarget);
  }

  @Test
  void shouldThrowExceptionIfSpellDoesntSupportSoManyTargets() {
    when(mage.getSpell()).thenReturn(Spell.FIRE_BOLT);

    assertThrows(UnsupportedSpellCastException.class, () -> spellCastOperation.invoke(mage, target, target));
  }

  @Test
  void shouldGetHitRollIfSpellIsTypeOfAttack() throws UnsupportedGameOperationException {
    when(mage.getName()).thenReturn(SOURCE);
    when(mage.getSpell()).thenReturn(Spell.FIRE_BOLT);
    Subject changedTarget = mock(Subject.class);
    when(target.of(anyInt())).thenReturn(changedTarget);

    spellCastOperation.invoke(mage, target);

    verify(hitRollSupplier).get(eq(mage), eq(target));
  }

  @Test
  void shouldNotGetHitRollIfSpellIsTypeOfIrresistible() throws UnsupportedGameOperationException {
    when(mage.getName()).thenReturn(SOURCE);
    when(mage.getSpell()).thenReturn(Spell.COLOR_SPRAY);
    Subject changedTarget = mock(Subject.class);
    when(target.of(eq(Effect.BLIND))).thenReturn(changedTarget);

    spellCastOperation.invoke(mage, target);

    verifyZeroInteractions(hitRollSupplier);
  }

  @Test
  void shouldThrowExceptionForUnsupportedDamageType() {
    Spell spell = mock(Spell.class);
    when(spell.getDamageType()).thenReturn("test");
    when(mage.getSpell()).thenReturn(spell);

    assertThrows(UnsupportedSpellCastException.class, () -> spellCastOperation.invoke(mage, target));
  }

  @Test
  void shouldInvokeEffectConsumer() throws UnsupportedGameOperationException {
    when(mage.getName()).thenReturn(SOURCE);
    when(mage.getSpell()).thenReturn(Spell.FIRE_BOLT);
    Subject changedTarget = mock(Subject.class);
    when(target.of(anyInt())).thenReturn(changedTarget);

    spellCastOperation.invoke(mage, target);

    verify(effectConsumer).apply(eq(mage));
  }

  @Test
  void shouldSendSuccessEventOnSuccessfulSpellCast() throws UnsupportedGameOperationException {
    when(mage.getName()).thenReturn(SOURCE);
    when(mage.getSpell()).thenReturn(Spell.COLOR_SPRAY);
    Subject changedTarget = mock(Subject.class);
    when(target.of(eq(Effect.BLIND))).thenReturn(changedTarget);

    spellCastOperation.invoke(mage, target);

    verify(eventsFactory).successSpellCastEvent(any(), any(), anyBoolean(), any());
    verify(eventHub).send(any());
  }

  @Test
  void shouldSendFailEventOnUnsuccessfulSpellCast() throws UnsupportedGameOperationException {
    when(mage.getName()).thenReturn(SOURCE);
    when(mage.getSpell()).thenReturn(Spell.FIRE_BOLT);
    when(target.getName()).thenReturn(TARGET);
    when(target.getArmorClass()).thenReturn(100);

    spellCastOperation.invoke(mage, target);

    verify(eventsFactory).failEvent(any(), any());
    verify(eventHub).send(any());
  }
}