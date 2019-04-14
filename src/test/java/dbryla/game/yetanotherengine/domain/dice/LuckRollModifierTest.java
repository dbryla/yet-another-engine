package dbryla.game.yetanotherengine.domain.dice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LuckRollModifierTest {
  
  @InjectMocks
  private LuckRollModifier luckRollModifier;
  
  @Mock
  private DiceRollService diceRollService;

  @Test
  void shouldReturnNewRollIfOriginalRollIsOne() {
    when(diceRollService.k20()).thenReturn(5);

    int result = luckRollModifier.apply(1);
    
    assertThat(result).isEqualTo(5);
  }
  
  @Test
  void shouldReturnTheSameRollIfOriginalRollIsNotOne() {
    int result = luckRollModifier.apply(10);

    assertThat(result).isEqualTo(10);
  }
}