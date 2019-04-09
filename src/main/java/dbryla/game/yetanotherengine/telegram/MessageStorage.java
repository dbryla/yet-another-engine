package dbryla.game.yetanotherengine.telegram;

public interface MessageStorage {

  void add(Integer messageId, Session session);
}
