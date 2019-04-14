package dbryla.game.yetanotherengine.domain.dice;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DisadvantageRollModifier implements HitDiceRollModifier {
  private DiceRollService diceRollService;

  @Override
  public int apply(int originalDiceRoll) {
    return Math.min(originalDiceRoll, diceRollService.k20());
  }

  @Override
  public boolean canModifyOriginalHitRoll() {
    return true;
  }


}
