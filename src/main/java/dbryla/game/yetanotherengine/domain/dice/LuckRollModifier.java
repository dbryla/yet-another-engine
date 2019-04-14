package dbryla.game.yetanotherengine.domain.dice;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LuckRollModifier implements HitDiceRollModifier {
  private DiceRollService diceRollService;

  @Override
  public int apply(int originalDiceRoll) {
    if (originalDiceRoll == 1) {
      return diceRollService.k20();
    }
    return originalDiceRoll;
  }

  @Override
  public boolean canModifyOriginalHitRoll() {
    return true;
  }


}
