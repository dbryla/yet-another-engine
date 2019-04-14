package dbryla.game.yetanotherengine.domain.effects;

import dbryla.game.yetanotherengine.domain.dice.*;
import dbryla.game.yetanotherengine.domain.subject.ActiveEffect;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BlessEffect implements EffectLogic {

  private final BlessedRollModifier blessedRollModifier;
  private final NoOpRollModifier noOpRollModifier;

  @Override
  public HitDiceRollModifier getSourceHitRollModifier() {
    return blessedRollModifier;
  }

  @Override
  public HitDiceRollModifier getTargetHitRollModifier() {
    return noOpRollModifier;
  }

  @Override
  public HitDiceRollModifier getTargetSavingThrowModifier() {
    return blessedRollModifier;
  }

}
