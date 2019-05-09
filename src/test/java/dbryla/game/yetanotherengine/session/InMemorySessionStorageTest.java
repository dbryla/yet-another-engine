package dbryla.game.yetanotherengine.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class InMemorySessionStorageTest {

  private final SessionStorage sessionStorage = new InMemorySessionStorage();

  @Test
  void shouldStoreSession() {
    String sessionId = UUID.randomUUID().toString();
    FightSession session = mock(FightSession.class);

    sessionStorage.put(sessionId, session);

    assertThat(sessionStorage.getFightSession(sessionId)).isEqualTo(session);
  }

  @Test
  void shouldOverwriteSession() {
    String sessionId = UUID.randomUUID().toString();
    FightSession oldSession = mock(FightSession.class);
    sessionStorage.put(sessionId, oldSession);
    FightSession newSession = mock(FightSession.class);

    sessionStorage.put(sessionId, newSession);

    assertThat(sessionStorage.getFightSession(sessionId)).isEqualTo(newSession);
  }

}