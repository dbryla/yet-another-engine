package dbryla.game.yetanotherengine.telegram;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
class KeyboardFactory {

  EditMessageReplyMarkup editKeyboard(Communicate communicate, Long chatId, Integer messageId) {
    EditMessageReplyMarkup sendMessage = new EditMessageReplyMarkup();
    sendMessage.setChatId(chatId);
    sendMessage.setMessageId(messageId);
    InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
    keyboardMarkup.setKeyboard(communicate.getKeyboardButtons());
    sendMessage.setReplyMarkup(keyboardMarkup);
    return sendMessage;
  }

  SendMessage replyKeyboard(Communicate communicate, Long chatId, Integer messageId) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.setText(communicate.getText());
    sendMessage.setChatId(chatId);
    sendMessage.setReplyToMessageId(messageId);
    InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
    keyboardMarkup.setKeyboard(communicate.getKeyboardButtons());
    sendMessage.setReplyMarkup(keyboardMarkup);
    return sendMessage;
  }
}
