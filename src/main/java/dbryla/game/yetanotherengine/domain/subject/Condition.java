package dbryla.game.yetanotherengine.domain.subject;

import dbryla.game.yetanotherengine.domain.effects.Effect;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Condition {

  private final Effect effect;
  private int durationInTurns;
  private String source;

  public Condition(Effect effect, int durationInTurns) {
    this.effect = effect;
    this.durationInTurns = durationInTurns;
  }

  public void decreaseDuration() {
    --durationInTurns;
  }

  @Override
  public String toString() {
    return effect.toString();
  }
}
