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

}