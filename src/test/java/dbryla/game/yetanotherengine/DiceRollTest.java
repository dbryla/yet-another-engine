package dbryla.game.yetanotherengine;

import static org.assertj.core.api.Assertions.assertThat;

import dbryla.game.yetanotherengine.domain.DiceRoll;
import org.junit.jupiter.api.Test;

class DiceRollTest {

  @Test
  void shouldReturnValueBetween1And4Inclusive() {
    int result = DiceRoll.k4();

    assertThat(result)
        .isGreaterThanOrEqualTo(1)
        .isLessThanOrEqualTo(4);
  }

  @Test
  void shouldReturnValueBetween1And6Inclusive() {
    int result = DiceRoll.k6();

    assertThat(result)
        .isGreaterThanOrEqualTo(1)
        .isLessThanOrEqualTo(6);
  }

  @Test
  void shouldReturnValueBetween1And8Inclusive() {
    int result = DiceRoll.k8();

    assertThat(result)
        .isGreaterThanOrEqualTo(1)
        .isLessThanOrEqualTo(8);
  }

  @Test
  void shouldReturnValueBetween1And10Inclusive() {
    int result = DiceRoll.k10();

    assertThat(result)
        .isGreaterThanOrEqualTo(1)
        .isLessThanOrEqualTo(10);
  }

  @Test
  void shouldReturnValueBetween1And20Inclusive() {
    int result = DiceRoll.k20();

    assertThat(result)
        .isGreaterThanOrEqualTo(1)
        .isLessThanOrEqualTo(20);
  }
}