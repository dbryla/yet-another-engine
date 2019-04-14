package dbryla.game.yetanotherengine;

import static org.assertj.core.api.Assertions.assertThat;

import dbryla.game.yetanotherengine.domain.dice.DiceRollService;
import org.junit.jupiter.api.Test;

import java.util.Random;

class DiceRollServiceTest {

  private DiceRollService diceRollService = new DiceRollService(new Random());

  @Test
  void shouldReturnValueBetween1And4Inclusive() {
    int result = diceRollService.k4();

    assertThat(result)
        .isGreaterThanOrEqualTo(1)
        .isLessThanOrEqualTo(4);
  }

  @Test
  void shouldReturnValueBetween1And6Inclusive() {
    int result = diceRollService.k6();

    assertThat(result)
        .isGreaterThanOrEqualTo(1)
        .isLessThanOrEqualTo(6);
  }

  @Test
  void shouldReturnValueBetween1And8Inclusive() {
    int result = diceRollService.k8();

    assertThat(result)
        .isGreaterThanOrEqualTo(1)
        .isLessThanOrEqualTo(8);
  }

  @Test
  void shouldReturnValueBetween1And10Inclusive() {
    int result = diceRollService.k10();

    assertThat(result)
        .isGreaterThanOrEqualTo(1)
        .isLessThanOrEqualTo(10);
  }

  @Test
  void shouldReturnValueBetween1And20Inclusive() {
    int result = diceRollService.k20();

    assertThat(result)
        .isGreaterThanOrEqualTo(1)
        .isLessThanOrEqualTo(20);
  }
}