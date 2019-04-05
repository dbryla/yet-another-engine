package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.DiceRollModifier;
import dbryla.game.yetanotherengine.domain.spells.Effect;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
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
  void shouldReturnSourceModifierIfEffectIsActiveOnIt() {
    DiceRollModifier diceRollModifier = mock(DiceRollModifier.class);
    when(diceRollModifier.getDiceRollModifier()).thenReturn(10);
    Effect effect = mock(Effect.class);
    when(effect.getSourceModifier()).thenReturn(diceRollModifier);
    Subject source = mock(Subject.class);
    when(source.getActiveEffect()).thenReturn(Optional.of(effect));

    int result = fightHelper.getHitRoll(source, null);

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

    int result = fightHelper.getHitRoll(source, target);

    assertThat(result).isEqualTo(0);
  }

  @Test
  void shouldMissAttackIfRollLessThanArmorClass() {
    assertThat(fightHelper.isMiss(10, 1)).isTrue();
  }

  @Test
  void shouldMissAttackIfRollOne() {
    assertThat(fightHelper.isMiss(0, 1)).isTrue();
  }

  @Test
  void shouldNotMissAttackIfRollMoreThanArmorClass() {
    assertThat(fightHelper.isMiss(5, 10)).isFalse();
  }

  @Test
  void shouldNotMissAttackIfRollTheSameArmorClass() {
    assertThat(fightHelper.isMiss(10, 10)).isFalse();
  }

  @Test
  void shouldGetUnchangedAttackDamageIfDidntRollTwenty() {
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