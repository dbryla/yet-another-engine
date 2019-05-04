package dbryla.game.yetanotherengine.domain.effects;

import dbryla.game.yetanotherengine.domain.subject.ActiveEffect;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Effect {
  BLIND, BLESS, LUCKY, RELENTLESS_ENDURANCE, ADVANTAGE_ON_SAVING_THROWS_AGAINST_POISON, RESISTANCE_AGAINST_POISON, MULTI_ATTACK;

  public ActiveEffect activate(int durationInTurns) {
    return new ActiveEffect(this, durationInTurns);
  }

  @Override
  public String toString() {
    return super.toString().toLowerCase().replace("_", " ");
  }
}
