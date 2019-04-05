package dbryla.game.yetanotherengine.domain.operations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dbryla.game.yetanotherengine.domain.DiceRollModifier;
import dbryla.game.yetanotherengine.domain.spells.Effect;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class HitRollSupplierTest {

  private HitRollSupplier hitRollSupplier = new HitRollSupplier();

  @Test
  void shouldReturnWeaponHitRollIfNoEffectIsActive() {
    Subject source = mock(Subject.class);
    Subject target = mock(Subject.class);
    when(source.getActiveEffect()).thenReturn(Optional.empty());
    when(source.calculateWeaponHitRoll()).thenReturn(20);

    int result = hitRollSupplier.get(source, target);

    assertThat(result).isEqualTo(20);
  }

  @Test
  void shouldReturnSourceModifierIfEffectIsActiveOnIt() {
    DiceRollModifier diceRollModifier = mock(DiceRollModifier.class);
    when(diceRollModifier.getDiceRollModifier()).thenReturn(10);
    Effect effect = mock(Effect.class);
    when(effect.getSourceModifier()).thenReturn(diceRollModifier);
    Subject source = mock(Subject.class);
    when(source.getActiveEffect()).thenReturn(Optional.of(effect));

    int result = hitRollSupplier.get(source, null);

    assertThat(result).isEqualTo(10);
  }

  @Test
  void shouldReturnTargetModifierIfEffectIsActiveOnIt() {
    DiceRollModifier diceRollModifier = mock(DiceRollModifier.class);
    when(diceRollModifier.getDiceRollModifier()).thenReturn(0);
    Effect effect = mock(Effect.class);
    when(effect.getTargetModifier()).thenReturn(diceRollModifier);
    Subject source = mock(Subject.class);
    when(source.getActiveEffect()).thenReturn(Optional.empty());
    Subject target = mock(Subject.class);
    when(target.getActiveEffect()).thenReturn(Optional.of(effect));

    int result = hitRollSupplier.get(source, target);

    assertThat(result).isEqualTo(0);
  }
}