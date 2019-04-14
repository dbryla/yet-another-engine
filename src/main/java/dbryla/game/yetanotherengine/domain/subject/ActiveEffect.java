package dbryla.game.yetanotherengine.domain.subject;

import dbryla.game.yetanotherengine.domain.effects.Effect;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ActiveEffect {

  private final Effect effect;
  private int durationInTurns;

  public void decreaseDuration() {
    --durationInTurns;
  }

  @Override
  public String toString() {
    return effect.toString();
  }
}
