package dbryla.game.yetanotherengine.domain.operations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dbryla.game.yetanotherengine.domain.spells.DiceRollModifier;
import dbryla.game.yetanotherengine.domain.spells.Effect;
import dbryla.game.yetanotherengine.domain.subjects.ActiveEffect;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class FightHelperTest {

  private FightHelper fightHelper = new FightHelper();

  @Test
  void shouldReturnRandomHitRollIfNoEffectIsActive() {
    Subject source = mock(Subject.class);
    Subject target = mock(Subject.class);
    when(source.getActiveEffect()).thenReturn(Optional.empty());

    HitRoll result = fightHelper.getHitRoll(source, target);

    assertThat(result.getActual()).isGreaterThanOrEqualTo(1).isLessThanOrEqualTo(20);
  }

  @Test
  void shouldAddSourceModifierIfEffectIsActiveOnIt() {
    DiceRollModifier diceRollModifier = mock(DiceRollModifier.class);
    when(diceRollModifier.getDiceRollModifier()).thenReturn(100);
    Effect effect = mock(Effect.class);
    when(effect.getSourceHitRollModifier()).thenReturn(diceRollModifier);
    Subject source = mock(Subject.class);
    when(source.getActiveEffect()).thenReturn(Optional.of(new ActiveEffect(effect, 1)));
    Subject target = mock(Subject.class);

    HitRoll result = fightHelper.getHitRoll(source, target);

    assertThat(result.getActual()).isGreaterThan(100);
  }

  @Test
  void shouldAddTargetModifierIfEffectIsActiveOnIt() {
    DiceRollModifier diceRollModifier = mock(DiceRollModifier.class);
    when(diceRollModifier.getDiceRollModifier()).thenReturn(100);
    Effect effect = mock(Effect.class);
    when(effect.getTargetHitRollModifier()).thenReturn(diceRollModifier);
    Subject source = mock(Subject.class);
    when(source.getActiveEffect()).thenReturn(Optional.empty());
    Subject target = mock(Subject.class);
    when(target.getActiveEffect()).thenReturn(Optional.of(new ActiveEffect(effect, 1)));

    HitRoll result = fightHelper.getHitRoll(source, target);

    assertThat(result.getActual()).isGreaterThan(0);
  }

  @Test
  void shouldGetUnchangedAttackDamageIfDidNotRollTwenty() {
    int attackDamage = 10;

    int result = fightHelper.getAttackDamage(attackDamage, HitResult.HIT);

    assertThat(result).isEqualTo(attackDamage);
  }

  @Test
  void shouldGetDoubledAttackDamageIfRollTwenty() {
    int attackDamage = 10;

    int result = fightHelper.getAttackDamage(attackDamage, HitResult.CRITICAL);

    assertThat(result).isEqualTo(2 * attackDamage);
  }
}