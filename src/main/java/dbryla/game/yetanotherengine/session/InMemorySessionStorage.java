package dbryla.game.yetanotherengine.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class InMemorySessionStorage implements SessionStorage {

  private Map<String, FightSession> fightSessions = new ConcurrentHashMap<>();
  private Map<String, BuildSession> buildSessions = new ConcurrentHashMap<>();

  @Override
  public void put(String sessionId, FightSession session) {
    fightSessions.put(sessionId, session);
  }

  @Override
  public FightSession getFightSession(String sessionId) {
    return fightSessions.get(sessionId);
  }

  @Override
  public void put(String sessionId, BuildSession session) {
    buildSessions.put(sessionId, session);
  }

  @Override
  public BuildSession getBuildSession(String sessionId) {
    return buildSessions.get(sessionId);
  }

}
