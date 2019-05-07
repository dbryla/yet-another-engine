package dbryla.game.yetanotherengine.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class TelegramClient extends DefaultAbsSender {

  private final TelegramConfig telegramConfig;
  private final KeyboardFactory keyboardFactory;

  public TelegramClient(TelegramConfig telegramConfig, KeyboardFactory keyboardFactory) {
    super(new DefaultBotOptions());
    this.telegramConfig = telegramConfig;
    this.keyboardFactory = keyboardFactory;
  }

  @Override
  public String getBotToken() {
    return telegramConfig.getToken();
  }

  public void sendTextMessage(Long chatId, String text) {
    sendMessage(new SendMessage()
        .setChatId(chatId)
        .setText(text));
  }

  private void sendMessage(BotApiMethod message) {
    try {
      execute(message);
    } catch (TelegramApiException e) {
      log.error("Error while sending message to Telegram", e);
    }
  }

  public void sendReplyKeyboard(Communicate communicate, Long chatId, Integer messageId) {
    sendMessage(keyboardFactory.replyKeyboard(communicate, chatId, messageId));
  }

  public void sendEditKeyboard(Communicate communicate, Long chatId, Integer messageId) {
    sendMessage(keyboardFactory.editKeyboard(communicate, chatId, messageId));
  }

  public void deleteMessage(Long chatId, Integer messageId) {
    sendMessage(new DeleteMessage(chatId, messageId));
  }
}
