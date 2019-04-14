package dbryla.game.yetanotherengine.session;

import dbryla.game.yetanotherengine.domain.game.Game;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class InMemorySessionStorage implements SessionStorage {

  private Map<String, Session> sessions = new ConcurrentHashMap<>();
  private Map<Long, Game> games = new ConcurrentHashMap<>();

  @Override
  public void put(String sessionId, Session session) {
    sessions.put(sessionId, session);
  }

  @Override
  public Session get(String sessionId) {
    return sessions.get(sessionId);
  }

  @Override
  public void put(Long gameId, Game game) {
    games.putIfAbsent(gameId, game);
  }

  @Override
  public Game get(Long gameId) {
    return games.get(gameId);
  }

  @Override
  public void clear(Long gameId) {
    games.computeIfPresent(gameId, (id, game) -> {
          game.cleanup();
          return null;
        }
    );
  }
}
