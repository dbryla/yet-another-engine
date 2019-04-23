package dbryla.game.yetanotherengine.session;

import dbryla.game.yetanotherengine.domain.game.Game;

public interface SessionStorage {

  void put(String sessionId, Session session);

  Session get(String sessionId);

  void put(Long gameId, Game game);

  Game get(Long gameId);

  void clearGame(Long gameId);
}
