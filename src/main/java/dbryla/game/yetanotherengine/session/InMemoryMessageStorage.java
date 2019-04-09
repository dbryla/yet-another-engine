package dbryla.game.yetanotherengine.session;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryMessageStorage implements MessageStorage{

  private Map<Integer, Session> sessions = new ConcurrentHashMap<>();

  @Override
  public void put(Integer messageId, Session session) {
    sessions.put(messageId, session);
  }
}
