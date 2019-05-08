package dbryla.game.yetanotherengine.telegram.callback;

import static dbryla.game.yetanotherengine.telegram.CommunicateText.ABILITIES;

import dbryla.game.yetanotherengine.session.Session;
import dbryla.game.yetanotherengine.telegram.BuildingFactory;
import dbryla.game.yetanotherengine.telegram.Communicate;
import dbryla.game.yetanotherengine.telegram.SessionFactory;
import dbryla.game.yetanotherengine.telegram.TelegramClient;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Profile("tg")
public class AbilitiesCallbackHandler implements CallbackHandler {

  private final SessionFactory sessionFactory;
  private final TelegramClient telegramClient;
  private final BuildingFactory buildingFactory;

  @Override
  public void execute(Callback callback) {
    Session session = sessionFactory.getSession(callback.getSessionId());
    session.addAbility(callback.getData());
    buildingFactory.nextAbilityAssignment(session, callback.getData()).ifPresent(session::addNextCommunicate);
    Communicate communicate = session.getNextCommunicate();
    if (ABILITIES.equals(communicate.getText())) {
      telegramClient.sendEditKeyboard(communicate, callback.getChatId(), callback.getMessageId());
    } else {
      telegramClient.deleteMessage(callback.getChatId(), callback.getMessageId());
      telegramClient.sendReplyKeyboard(communicate, callback.getChatId(), callback.getOriginalMessageId());
    }
  }
}
