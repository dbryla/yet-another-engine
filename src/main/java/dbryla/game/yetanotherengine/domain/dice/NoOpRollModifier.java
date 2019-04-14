package dbryla.game.yetanotherengine.domain.dice;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class NoOpRollModifier implements HitDiceRollModifier {

  @Override
  public int apply(int originalDiceRoll) {
    return originalDiceRoll;
  }

  @Override
  public boolean canModifyOriginalHitRoll() {
    return false;
  }

}
