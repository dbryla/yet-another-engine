package dbryla.game.yetanotherengine.telegram.callback;

import dbryla.game.yetanotherengine.session.Session;
import dbryla.game.yetanotherengine.telegram.SessionFactory;
import dbryla.game.yetanotherengine.telegram.TelegramClient;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Profile("tg")
public class ArmorCallbackHandler implements CallbackHandler {

  private final SessionFactory sessionFactory;
  private final TelegramClient telegramClient;
  private final CharacterCreationCallbackHandler characterCreationCallbackHandler;

  @Override
  public void execute(Callback callback) {
    Session session = sessionFactory.getSession(callback.getSessionId());
    session.setArmor(callback.getData());
    telegramClient.deleteMessage(callback.getChatId(), callback.getMessageId());
    characterCreationCallbackHandler.execute(callback);
  }
}
