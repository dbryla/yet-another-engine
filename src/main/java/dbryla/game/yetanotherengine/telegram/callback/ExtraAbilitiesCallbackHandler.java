package dbryla.game.yetanotherengine.telegram.callback;

import dbryla.game.yetanotherengine.session.BuildSession;
import dbryla.game.yetanotherengine.telegram.BuildingFactory;
import dbryla.game.yetanotherengine.telegram.SessionFactory;
import dbryla.game.yetanotherengine.telegram.TelegramClient;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Profile("tg")
public class ExtraAbilitiesCallbackHandler implements CallbackHandler {

  private final SessionFactory sessionFactory;
  private final TelegramClient telegramClient;
  private final BuildingFactory buildingFactory;

  @Override
  public void execute(Callback callback) {
    BuildSession session = sessionFactory.getBuildSession(callback.getSessionId());
    int index = Integer.parseInt(callback.getData());
    session.addAbilityToImprove(index);
    buildingFactory.extraAbilitiesCommunicate(session, callback.getData()).ifPresent(session::addNextCommunicate);
    telegramClient.deleteMessage(callback.getChatId(), callback.getMessageId());
    telegramClient.sendReplyKeyboard(session.getNextCommunicate(), callback.getChatId(), callback.getOriginalMessageId());
  }
}
