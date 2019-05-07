package dbryla.game.yetanotherengine.telegram.session;

import dbryla.game.yetanotherengine.domain.game.Game;

public interface GameStorage {

  void put(Long gameId, Game game);

  Game get(Long gameId);

  void clearGame(Long gameId);
}
