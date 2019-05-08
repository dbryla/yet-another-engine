package dbryla.game.yetanotherengine.telegram.callback;

import lombok.Data;

@Data
public class Callback {

  private final Integer messageId;
  private final String playerName;
  private final Long chatId;
  private final String sessionId;
  private final String data;
  private final Integer originalMessageId;

}
