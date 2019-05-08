package dbryla.game.yetanotherengine.domain.effects;

import dbryla.game.yetanotherengine.domain.subject.Condition;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Effect {
  BLESSED,

  BLINDED, CHARMED, DEAFENED, FRIGHTENED, GRAPPLED, INCAPACITATED, INVISIBLE,
  PARALYZED, PETRIFIED, POISONED, PRONE, RESTRAINED, STUNNED, UNCONSCIOUS,

  EXHAUSTION, SLEEP,

  LUCKY, RELENTLESS_ENDURANCE, SAVAGE_ATTACK,

  MULTI_ATTACK;

  public Condition activate(int durationInTurns) {
    return new Condition(this, durationInTurns);
  }

  public Condition activate(String source, int durationInTurns) {
    return new Condition(this, durationInTurns, source);
  }

  @Override
  public String toString() {
    return super.toString().toLowerCase().replace("_", " ");
  }
}
