package dbryla.game.yetanotherengine.domain.effects;

import dbryla.game.yetanotherengine.domain.dice.DisadvantageRollModifier;
import dbryla.game.yetanotherengine.domain.dice.HitDiceRollModifier;
import dbryla.game.yetanotherengine.domain.dice.NoOpRollModifier;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MultiAttackEffect implements EffectLogic {

  private final NoOpRollModifier noOpRollModifier;
  private final DisadvantageRollModifier disadvantageRollModifier;

  @Override
  public HitDiceRollModifier getSourceHitRollModifier() {
    return disadvantageRollModifier;
  }

  @Override
  public HitDiceRollModifier getTargetHitRollModifier() {
    return noOpRollModifier;
  }

  @Override
  public HitDiceRollModifier getTargetSavingThrowModifier() {
    return noOpRollModifier;
  }
}
