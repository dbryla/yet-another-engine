package dbryla.game.yetanotherengine.session;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemorySessionStorage implements SessionStorage {

  private Map<String, Session> sessions = new ConcurrentHashMap<>();

  @Override
  public void put(String sessionId, Session session) {
    sessions.put(sessionId, session);
  }

  @Override
  public Session get(String sessionId) {
    return sessions.get(sessionId);
  }
}
