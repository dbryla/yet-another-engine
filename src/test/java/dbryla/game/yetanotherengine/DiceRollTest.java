package dbryla.game.yetanotherengine;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DiceRollTest {

  @Test
  void shouldReturnValueBetween1And6Inclusive() {
    int result = DiceRoll.k6();

    assertThat(result).isGreaterThanOrEqualTo(1);
    assertThat(result).isLessThanOrEqualTo(6);
  }

  @Test
  void shouldReturnValueBetween1And20Inclusive() {
    int result = DiceRoll.k20();

    assertThat(result).isGreaterThanOrEqualTo(1);
    assertThat(result).isLessThanOrEqualTo(20);
  }
}