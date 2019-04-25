package dbryla.game.yetanotherengine.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import dbryla.game.yetanotherengine.domain.game.Game;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class InMemorySessionStorageTest {

  private final SessionStorage sessionStorage = new InMemorySessionStorage();

  private final Random random = new Random();

  @Test
  void shouldStoreSession() {
    String sessionId = UUID.randomUUID().toString();
    Session session = mock(Session.class);

    sessionStorage.put(sessionId, session);

    assertThat(sessionStorage.get(sessionId)).isEqualTo(session);
  }

  @Test
  void shouldOverwriteSession() {
    String sessionId = UUID.randomUUID().toString();
    Session oldSession = mock(Session.class);
    sessionStorage.put(sessionId, oldSession);
    Session newSession = mock(Session.class);

    sessionStorage.put(sessionId, newSession);

    assertThat(sessionStorage.get(sessionId)).isEqualTo(newSession);
  }

  @Test
  void shouldStoreGame() {
    Long gameId = random.nextLong();
    Game game = mock(Game.class);

    sessionStorage.put(gameId, game);

    assertThat(sessionStorage.get(gameId)).isEqualTo(game);
  }

  @Test
  void shouldNotOverwriteGame() {
    Long gameId = random.nextLong();
    Game oldGame = mock(Game.class);
    sessionStorage.put(gameId, oldGame);
    Game newGame = mock(Game.class);

    sessionStorage.put(gameId, newGame);

    assertThat(sessionStorage.get(gameId)).isEqualTo(oldGame);
  }

  @Test
  void shouldRemoveAndCleanupGame() {
    Long gameId = random.nextLong();
    Game game = mock(Game.class);
    sessionStorage.put(gameId, game);

    sessionStorage.clearGame(gameId);

    assertThat(sessionStorage.get(gameId)).isNull();
    verify(game).cleanup();
  }
}