package dbryla.game.yetanotherengine.domain.effects;

import dbryla.game.yetanotherengine.domain.subject.Condition;
import lombok.Getter;

public enum Effect {
  BLESSED,

  BLINDED, CHARMED, DEAFENED, FRIGHTENED, GRAPPLED, INCAPACITATED, INVISIBLE,
  PARALYZED, PETRIFIED, POISONED, PRONE, RESTRAINED, STUNNED, UNCONSCIOUS,

  EXHAUSTION, SLEEP,

  LUCKY, RELENTLESS_ENDURANCE, SAVAGE_ATTACK,

  MULTI_ATTACK(false);

  @Getter
  private final boolean visible;

  Effect() {
    visible = true;
  }

  Effect(boolean visible) {
    this.visible = visible;
  }

  public Condition activate(int durationInTurns) {
    return new Condition(this, durationInTurns);
  }

  @Override
  public String toString() {
    return super.toString().toLowerCase().replace("_", " ");
  }

}
