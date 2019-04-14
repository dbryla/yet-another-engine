package dbryla.game.yetanotherengine.domain.effects;

import dbryla.game.yetanotherengine.domain.dice.HitDiceRollModifier;
import dbryla.game.yetanotherengine.domain.dice.LuckRollModifier;
import dbryla.game.yetanotherengine.domain.dice.NoOpRollModifier;
import dbryla.game.yetanotherengine.domain.subject.ActiveEffect;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LuckyEffect implements EffectLogic {

  private final LuckRollModifier luckRollModifier;
  private final NoOpRollModifier noOpRollModifier;

  @Override
  public HitDiceRollModifier getSourceHitRollModifier() {
    return luckRollModifier;
  }

  @Override
  public HitDiceRollModifier getTargetHitRollModifier() {
    return noOpRollModifier;
  }

  @Override
  public HitDiceRollModifier getTargetSavingThrowModifier() {
    return luckRollModifier;
  }

}
