package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.spells.DiceRollModifier;
import dbryla.game.yetanotherengine.domain.spells.Effect;
import dbryla.game.yetanotherengine.domain.subjects.classes.ActiveEffect;
import dbryla.game.yetanotherengine.domain.subjects.classes.Subject;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FightHelperTest {

  private FightHelper fightHelper = new FightHelper();

  @Test
  void shouldReturnRandomHitRollIfNoEffectIsActive() {
    Subject source = mock(Subject.class);
    Subject target = mock(Subject.class);
    when(source.getActiveEffect()).thenReturn(Optional.empty());

    int result = fightHelper.getHitRoll(source, target);

    assertThat(result).isGreaterThanOrEqualTo(1).isLessThanOrEqualTo(20);
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

    int result = fightHelper.getHitRoll(source, target);

    assertThat(result).isGreaterThan(100);
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

    int result = fightHelper.getHitRoll(source, target);

    assertThat(result).isGreaterThan(0);
  }

  @Test
  void shouldMissAttackIfRollLessThanArmorClass() {
    assertThat(fightHelper.isMiss(10, 6, 5)).isTrue();
  }

  @Test
  void shouldMissAttackIfOriginalRollIsOne() {
    assertThat(fightHelper.isMiss(1, 19, 1)).isTrue();
  }

  @Test
  void shouldNotMissAttackIfRollMoreThanArmorClass() {
    assertThat(fightHelper.isMiss(5, 10, 10)).isFalse();
  }

  @Test
  void shouldNotMissAttackIfRollTheSameArmorClass() {
    assertThat(fightHelper.isMiss(10, 10, 10)).isFalse();
  }

  @Test
  void shouldGetUnchangedAttackDamageIfDidNotRollTwenty() {
    int attackDamage = 10;

    int result = fightHelper.getAttackDamage(attackDamage, 10);

    assertThat(result).isEqualTo(attackDamage);
  }

  @Test
  void shouldGetDoubledAttackDamageIfRollTwenty() {
    int attackDamage = 10;

    int result = fightHelper.getAttackDamage(attackDamage, 20);

    assertThat(result).isEqualTo(2 * attackDamage);
  }
}