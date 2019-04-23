package dbryla.game.yetanotherengine.domain.ai;

import dbryla.game.yetanotherengine.domain.game.Game;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ArtificialIntelligenceContext {

  private final Game game;
  private String acquiredTarget;

}
