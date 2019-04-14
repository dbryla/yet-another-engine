package dbryla.game.yetanotherengine.domain.effects;

import dbryla.game.yetanotherengine.domain.dice.AdvantageRollModifier;
import dbryla.game.yetanotherengine.domain.dice.DisadvantageRollModifier;
import dbryla.game.yetanotherengine.domain.dice.HitDiceRollModifier;
import dbryla.game.yetanotherengine.domain.dice.NoOpRollModifier;
import dbryla.game.yetanotherengine.domain.subject.ActiveEffect;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BlindEffect implements EffectLogic {

  private final AdvantageRollModifier advantageRollModifier;
  private final DisadvantageRollModifier disadvantageRollModifier;
  private final NoOpRollModifier noOpRollModifier;

  @Override
  public HitDiceRollModifier getSourceHitRollModifier() {
    return disadvantageRollModifier;
  }

  @Override
  public HitDiceRollModifier getTargetHitRollModifier() {
    return advantageRollModifier;
  }

  @Override
  public HitDiceRollModifier getTargetSavingThrowModifier() {
    return noOpRollModifier;
  }

}
