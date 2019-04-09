package dbryla.game.yetanotherengine.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AbilitiesTest {

  @Test
  void shouldReturnProperNegativeModifier() {
    Abilities abilities = new Abilities(8, 7, 10, 10, 10, 10);

    assertThat(abilities.getDexterityModifier()).isEqualTo(-2);
    assertThat(abilities.getStrengthModifier()).isEqualTo(-1);
  }
}