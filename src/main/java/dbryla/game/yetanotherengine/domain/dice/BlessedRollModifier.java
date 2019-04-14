package dbryla.game.yetanotherengine.domain.dice;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BlessedRollModifier implements HitDiceRollModifier {
  private DiceRollService diceRollService;

  @Override
  public int apply(int originalDiceRoll) {
    return diceRollService.k4();
  }

  @Override
  public boolean canModifyOriginalHitRoll() {
    return false;
  }

}
