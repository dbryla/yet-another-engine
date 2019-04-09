package dbryla.game.yetanotherengine.session;

public interface MessageStorage {

  void put(Integer messageId, Session session);

  Session get(Integer messageId);
}
