package dbryla.game.yetanotherengine.session;

public interface SessionStorage {

  void put(String sessionId, FightSession session);

  FightSession getFightSession(String sessionId);

  void put(String sessionId, BuildSession session);

  BuildSession getBuildSession(String sessionId);

}
