package dbryla.game.yetanotherengine.domain.battleground;

public enum Position {
  PLAYERS_BACK, PLAYERS_FRONT, MID, ENEMIES_FRONT, ENEMIES_BACK;

  @Override
  public String toString() {
    return super.toString().replace("_", " ").toLowerCase();
  }
}
