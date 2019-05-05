package dbryla.game.yetanotherengine.domain.effects;

import dbryla.game.yetanotherengine.domain.Range;
import dbryla.game.yetanotherengine.domain.dice.DisadvantageRollModifier;
import dbryla.game.yetanotherengine.domain.dice.HitDiceRollModifier;
import dbryla.game.yetanotherengine.domain.dice.NoOpRollModifier;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MultiAttackFightEffect implements FightEffectLogic {

  private final NoOpRollModifier noOpRollModifier;
  private final DisadvantageRollModifier disadvantageRollModifier;

  @Override
  public HitDiceRollModifier getSourceHitRollModifier() {
    return disadvantageRollModifier;
  }

  @Override
  public HitDiceRollModifier getTargetHitRollModifier(Range range) {
    return noOpRollModifier;
  }

  @Override
  public HitDiceRollModifier getTargetSavingThrowModifier() {
    return noOpRollModifier;
  }
}
