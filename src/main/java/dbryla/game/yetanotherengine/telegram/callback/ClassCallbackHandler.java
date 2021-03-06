package dbryla.game.yetanotherengine.telegram.callback;

import dbryla.game.yetanotherengine.session.BuildSession;
import dbryla.game.yetanotherengine.telegram.SessionFactory;
import dbryla.game.yetanotherengine.telegram.TelegramClient;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Profile("tg")
public class ClassCallbackHandler implements CallbackHandler {

  private final SessionFactory sessionFactory;
  private final TelegramClient telegramClient;

  @Override
  public void execute(Callback callback) {
    BuildSession session = sessionFactory.getBuildSession(callback.getSessionId());
    session.setCharacterClass(callback.getData());
    telegramClient.deleteMessage(callback.getChatId(), callback.getMessageId());
    telegramClient.sendReplyKeyboard(session.getNextCommunicate(), callback.getChatId(), callback.getOriginalMessageId());
  }
}
