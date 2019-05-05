package dbryla.game.yetanotherengine.domain.effects;

import dbryla.game.yetanotherengine.domain.Range;
import dbryla.game.yetanotherengine.domain.dice.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BlessFightEffect implements FightEffectLogic {

  private final BlessedRollModifier blessedRollModifier;
  private final NoOpRollModifier noOpRollModifier;

  @Override
  public HitDiceRollModifier getSourceHitRollModifier() {
    return blessedRollModifier;
  }

  @Override
  public HitDiceRollModifier getTargetHitRollModifier(Range range) {
    return noOpRollModifier;
  }

  @Override
  public HitDiceRollModifier getTargetSavingThrowModifier() {
    return blessedRollModifier;
  }

}
