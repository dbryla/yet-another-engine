package dbryla.game.yetanotherengine.session;

public interface SessionStorage {

  void put(String sessionId, Session session);

  Session get(String sessionId);
}
