package dbryla.game.yetanotherengine.telegram;

import dbryla.game.yetanotherengine.InputProvider;
import dbryla.game.yetanotherengine.domain.Action;
import dbryla.game.yetanotherengine.domain.Game;
import dbryla.game.yetanotherengine.domain.GameOptions;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Profile("tg")
@Slf4j
@AllArgsConstructor
public class YetAnotherGameBot extends TelegramLongPollingBot implements InputProvider {

  private static final String START_COMMAND = "/start";
  private static final String JOIN_COMMAND = "/join";

  private final GameOptions gameOptions;
  private final MessageStorage messageStorage;

  @Override
  public void onUpdateReceived(Update update) {
    if (update.hasMessage() && update.getMessage().isCommand()) {
      log.trace("Command: {} [{}/{}]", update.getMessage().getText(), update.getMessage().getChatId(), update.getMessage().getMessageId());
      String commandText = update.getMessage().getText();
      if (commandText.startsWith(START_COMMAND)) {
        log.info("Starting new game.");
      }
      if (commandText.startsWith(JOIN_COMMAND)) {
        String playerName = update.getMessage().getFrom().getFirstName();
        messageStorage.add(update.getMessage().getMessageId(), new Session(playerName));
        sendClassChoice(update);
      }
    } else {
      if (update.hasMessage() && update.getMessage().hasText()) {
        log.trace("Update: {} [{}/{}]", update.getMessage().getText(), update.getMessage().getChatId(), update.getMessage().getMessageId());
        SendMessage message = new SendMessage()
            .setChatId(update.getMessage().getChatId())
            .setText("understand!");

        sendMessage(message);
      }
      log.trace("Update: {}", update);
    }
  }

  private void sendClassChoice(Update update) {
    SendMessage message = new SendMessage();
    message.setText("Choose a class:");
    message.setChatId(update.getMessage().getChatId());
    message.setReplyToMessageId(update.getMessage().getMessageId());
    ArrayList<Class> classes = new ArrayList<>(gameOptions.getAvailableClasses());
    InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
    List<InlineKeyboardButton> keyboardButtons = new LinkedList<>();
    classes.forEach(clazz -> keyboardButtons.add(new InlineKeyboardButton(clazz.getSimpleName()).setCallbackData(clazz.getSimpleName())));
    keyboardMarkup.setKeyboard(List.of(keyboardButtons));
    message.setReplyMarkup(keyboardMarkup);
    sendMessage(message);
  }

  private void sendMessage(SendMessage message) {
    try {
      execute(message);
    } catch (TelegramApiException e) {
      log.error("Error while sending message to Telegram", e);
    }
  }

  @Override
  public String getBotUsername() {
    return "YetAnotherGameBot";
  }

  @Override
  public String getBotToken() {
    return System.getenv("TELEGRAM_TOKEN");
  }

  @Override
  public Action askForAction(Subject subject, Game game) {
    return null;
  }
}
