package dbryla.game.yetanotherengine.domain.subject;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Affiliation {
  PLAYERS(1), ENEMIES(-1);

  private final int direction;
}
