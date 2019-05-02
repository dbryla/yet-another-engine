package dbryla.game.yetanotherengine.telegram;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KeyboardFactoryTest {

  private final KeyboardFactory keyboardFactory = new KeyboardFactory();

  @Test
  void shouldCreateEditKeyboard() {
    long chatId = 1L;
    int messageId = 2;

    EditMessageReplyMarkup editKeyboard = keyboardFactory.editKeyboard(new Communicate("", List.of()), chatId, messageId);

    assertThat(editKeyboard.getChatId()).isEqualTo(String.valueOf(chatId));
    assertThat(editKeyboard.getMessageId()).isEqualTo(messageId);
  }

  @Test
  void shouldCreateReplyKeyboard() {
    long chatId = 1L;
    int messageId = 2;

    SendMessage replyKeyboard = keyboardFactory.replyKeyboard(new Communicate("", List.of()), chatId, messageId);

    assertThat(replyKeyboard.getChatId()).isEqualTo(String.valueOf(chatId));
    assertThat(replyKeyboard.getReplyToMessageId()).isEqualTo(messageId);
  }
}