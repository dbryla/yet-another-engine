package dbryla.game.yetanotherengine.telegram.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import dbryla.game.yetanotherengine.domain.game.Game;
import java.util.Random;
import org.junit.jupiter.api.Test;

class InMemoryGameStorageTest {

  private final GameStorage gameStorage = new InMemoryGameStorage();

  private final Random random = new Random();

  @Test
  void shouldStoreGame() {
    Long gameId = random.nextLong();
    Game game = mock(Game.class);

    gameStorage.put(gameId, game);

    assertThat(gameStorage.get(gameId)).isEqualTo(game);
  }

  @Test
  void shouldNotOverwriteGame() {
    Long gameId = random.nextLong();
    Game oldGame = mock(Game.class);
    gameStorage.put(gameId, oldGame);
    Game newGame = mock(Game.class);

    gameStorage.put(gameId, newGame);

    assertThat(gameStorage.get(gameId)).isEqualTo(oldGame);
  }

  @Test
  void shouldRemoveAndCleanupGame() {
    Long gameId = random.nextLong();
    Game game = mock(Game.class);
    gameStorage.put(gameId, game);

    gameStorage.clearGame(gameId);

    assertThat(gameStorage.get(gameId)).isNull();
    verify(game).cleanup();
  }

}