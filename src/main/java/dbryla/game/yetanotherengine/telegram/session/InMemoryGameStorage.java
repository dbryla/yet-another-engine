package dbryla.game.yetanotherengine.telegram.session;

import dbryla.game.yetanotherengine.domain.game.Game;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class InMemoryGameStorage implements GameStorage {

  private Map<Long, Game> games = new ConcurrentHashMap<>();

  @Override
  public void put(Long gameId, Game game) {
    games.putIfAbsent(gameId, game);
  }

  @Override
  public Game get(Long gameId) {
    return games.get(gameId);
  }

  @Override
  public void clearGame(Long gameId) {
    games.computeIfPresent(gameId, (id, game) -> {
          game.cleanup();
          return null;
        }
    );
  }

}
