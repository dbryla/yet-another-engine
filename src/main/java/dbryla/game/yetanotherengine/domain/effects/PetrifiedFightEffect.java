package dbryla.game.yetanotherengine.domain.effects;

import dbryla.game.yetanotherengine.domain.Range;
import dbryla.game.yetanotherengine.domain.dice.AdvantageRollModifier;
import dbryla.game.yetanotherengine.domain.dice.HitDiceRollModifier;
import dbryla.game.yetanotherengine.domain.dice.NoOpRollModifier;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class PetrifiedFightEffect implements FightEffectLogic {

  private final NoOpRollModifier noOpRollModifier;
  private final AdvantageRollModifier advantageRollModifier;

  @Override
  public HitDiceRollModifier getSourceHitRollModifier() {
    return noOpRollModifier;
  }

  @Override
  public HitDiceRollModifier getTargetHitRollModifier(Range range) {
    return advantageRollModifier;
  }

  @Override
  public HitDiceRollModifier getTargetSavingThrowModifier() {
    return noOpRollModifier;
  }
}
