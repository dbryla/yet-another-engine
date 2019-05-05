package dbryla.game.yetanotherengine.domain.effects;

import dbryla.game.yetanotherengine.domain.Range;
import dbryla.game.yetanotherengine.domain.dice.HitDiceRollModifier;
import dbryla.game.yetanotherengine.domain.dice.LuckRollModifier;
import dbryla.game.yetanotherengine.domain.dice.NoOpRollModifier;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LuckyFightEffect implements FightEffectLogic {

  private final LuckRollModifier luckRollModifier;
  private final NoOpRollModifier noOpRollModifier;

  @Override
  public HitDiceRollModifier getSourceHitRollModifier() {
    return luckRollModifier;
  }

  @Override
  public HitDiceRollModifier getTargetHitRollModifier(Range range) {
    return noOpRollModifier;
  }

  @Override
  public HitDiceRollModifier getTargetSavingThrowModifier() {
    return luckRollModifier;
  }

}
