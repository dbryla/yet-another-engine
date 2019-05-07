package dbryla.game.yetanotherengine.telegram.callback;

import dbryla.game.yetanotherengine.session.Session;
import dbryla.game.yetanotherengine.telegram.Commons;
import dbryla.game.yetanotherengine.telegram.SessionFactory;
import dbryla.game.yetanotherengine.telegram.TelegramClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@AllArgsConstructor
public class ClassCallbackHandler implements CallbackHandler {

  private final SessionFactory sessionFactory;
  private final Commons commons;
  private final TelegramClient telegramClient;

  @Override
  public void execute(Update update) {
    Long chatId = update.getCallbackQuery().getMessage().getChatId();
    Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
    String sessionId = commons.getSessionId(update.getCallbackQuery().getMessage(), update.getCallbackQuery().getFrom());
    Session session = sessionFactory.getSession(sessionId);
    String callbackData = update.getCallbackQuery().getData();
    session.setCharacterClass(callbackData);
    Integer originalMessageId = commons.getOriginalMessageId(update);
    telegramClient.deleteMessage(chatId, messageId);
    telegramClient.sendReplyKeyboard(session.getNextCommunicate(), chatId, originalMessageId);
  }
}
