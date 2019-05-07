package dbryla.game.yetanotherengine.telegram.callback;

import dbryla.game.yetanotherengine.session.Session;
import dbryla.game.yetanotherengine.telegram.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static dbryla.game.yetanotherengine.telegram.CommunicateText.ABILITIES;

@Component
@AllArgsConstructor
public class AbilitiesCallbackHandler implements CallbackHandler {

  private final SessionFactory sessionFactory;
  private final Commons commons;
  private final TelegramClient telegramClient;
  private final BuildingFactory buildingFactory;

  @Override
  public void execute(Update update) {
    Long chatId = update.getCallbackQuery().getMessage().getChatId();
    Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
    String sessionId = commons.getSessionId(update.getCallbackQuery().getMessage(), update.getCallbackQuery().getFrom());
    Session session = sessionFactory.getSession(sessionId);
    String callbackData = update.getCallbackQuery().getData();
    session.addAbility(callbackData);
    buildingFactory.nextAbilityAssignment(session, callbackData).ifPresent(session::addNextCommunicate);
    Integer originalMessageId = commons.getOriginalMessageId(update);
    Communicate communicate = session.getNextCommunicate();
    if (ABILITIES.equals(communicate.getText())) {
      telegramClient.sendEditKeyboard(communicate, chatId, messageId);
    } else {
      telegramClient.deleteMessage(chatId, messageId);
      telegramClient.sendReplyKeyboard(communicate, chatId, originalMessageId);
    }
  }
}
