package dbryla.game.yetanotherengine.domain.battleground;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Position {
  PLAYERS_BACK(0), PLAYERS_FRONT(1), MID(2), ENEMIES_FRONT(3), ENEMIES_BACK(4);

  private final int battlegroundLocation;

  public static Position valueOf(int battlegroundPosition) {
    for (Position position : values()) {
      if (position.getBattlegroundLocation() == battlegroundPosition) {
        return position;
      }
    }
    throw new IllegalArgumentException("Position for " + battlegroundPosition + " doesn't exist.");
  }

  @Override
  public String toString() {
    return super.toString().replace("_", " ").toLowerCase();
  }
}
