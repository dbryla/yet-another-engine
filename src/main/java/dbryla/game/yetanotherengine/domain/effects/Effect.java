package dbryla.game.yetanotherengine.domain.effects;

import dbryla.game.yetanotherengine.domain.subject.ActiveEffect;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static dbryla.game.yetanotherengine.domain.effects.EffectLogic.CONCENTRATION;
import static dbryla.game.yetanotherengine.domain.effects.EffectLogic.FOREVER;

@AllArgsConstructor
public enum Effect {
  BLIND(1), BLESS(CONCENTRATION), LUCKY(FOREVER), RELENTLESS_ENDURANCE(FOREVER);

  @Getter
  private final int durationInTurns;

  public ActiveEffect activate() {
    return new ActiveEffect(this, durationInTurns);
  }

  @Override
  public String toString() {
    return super.toString().toLowerCase().replace("_", " ");
  }
}
