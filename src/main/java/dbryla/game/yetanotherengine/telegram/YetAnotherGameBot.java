package dbryla.game.yetanotherengine.telegram;

import dbryla.game.yetanotherengine.InputProvider;
import dbryla.game.yetanotherengine.domain.AbilityScoresSupplier;
import dbryla.game.yetanotherengine.domain.Action;
import dbryla.game.yetanotherengine.domain.Game;
import dbryla.game.yetanotherengine.domain.subjects.Subject;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import dbryla.game.yetanotherengine.session.SessionStorage;
import dbryla.game.yetanotherengine.session.Session;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static dbryla.game.yetanotherengine.telegram.CommunicateFactory.*;

@Component
@Profile("tg")
@Slf4j
@AllArgsConstructor
public class YetAnotherGameBot extends TelegramLongPollingBot implements InputProvider {

  private static final String START_COMMAND = "/start";
  private static final String JOIN_COMMAND = "/join";

  private final CommunicateFactory communicateFactory;
  private final SessionStorage sessionStorage;
  private final AbilityScoresSupplier abilityScoresSupplier;

  @Override
  public void onUpdateReceived(Update update) {
    if (update.hasMessage() && update.getMessage().isCommand()) {
      log.trace("Command: {} no:{} [{}]", update.getMessage().getText(), update.getMessage().getMessageId(),
          getSessionId(update.getMessage(), update.getMessage().getFrom()));
      String commandText = update.getMessage().getText();
      if (commandText.startsWith(START_COMMAND)) {
        log.info("Starting new game.");
      }
      if (commandText.startsWith(JOIN_COMMAND)) {
        handleJoinCommand(update);
      }
    } else {
      if (update.hasCallbackQuery()) {
        log.trace("Callback: {} no:{} [{}]", update.getCallbackQuery().getMessage().getText(),
            update.getCallbackQuery().getMessage().getMessageId(),
            getSessionId(update.getCallbackQuery().getMessage(),
                update.getCallbackQuery().getFrom()));
        handleCallback(update);
        return;
      }
      if (update.hasMessage() && update.getMessage().hasText()) {
        log.trace("Update: {} no:{} [{}]", update.getMessage().getText(), update.getMessage().getMessageId(),
            getSessionId(update.getMessage(), update.getMessage().getFrom()));
        SendMessage message = new SendMessage()
            .setChatId(update.getMessage().getChatId())
            .setText("understand!");

        log.info("Session: {}", sessionStorage.get(getSessionId(update.getMessage(), update.getMessage().getFrom())));
        sendMessage(message);
      }
    }
  }

  private String getSessionId(Message message, User user) {
    return message.getChatId() + "/" + user.getId();
  }

  private void handleJoinCommand(Update update) {
    String playerName = update.getMessage().getFrom().getFirstName();
    Message message = update.getMessage();
    Session session = buildSession(playerName, message.getMessageId());
    sessionStorage.put(getSessionId(message, message.getFrom()), session);
    Communicate communicate = session.getNextCommunicate().get();
    sendKeyboard(communicate, update.getMessage().getChatId(), session.getOriginalMessageId());
  }

  private Session buildSession(String playerName, Integer messageId) {
    List<Integer> abilityScores = abilityScoresSupplier.get();
    return new Session(playerName, messageId, new LinkedList<>(List.of(communicateFactory.chooseClassCommunicate(),
        communicateFactory.assignAbilitiesCommunicate(abilityScores))), abilityScores);
  }

  private void handleCallback(Update update) {
    Session session = sessionStorage
        .get(getSessionId(update.getCallbackQuery().getMessage(), update.getCallbackQuery().getFrom()));
    String callbackData = update.getCallbackQuery().getData();
    session.update(update.getCallbackQuery().getMessage().getText(), callbackData);
    Integer originalMessageId = update.getCallbackQuery().getMessage().getMessageId();
    Long chatId = update.getCallbackQuery().getMessage().getChatId();
    if (assigningAbilities(update, session) && session.getAbilityScores().size() > 1) {
      session.addNextCommunicate(
          communicateFactory.nextAbilityAssignment(session, callbackData));
    }
    session.getNextCommunicate().ifPresentOrElse(communicate -> {
      if (assigningAbilities(update, session) && ABILITIES.equals(communicate.getText())) {
        sendEditKeyboard(communicate, chatId, originalMessageId);
      } else {
        sendMessage(new DeleteMessage(chatId, originalMessageId));
        sendKeyboard(communicate, chatId, session.getOriginalMessageId());
      }
    }, () -> sendMessage(new DeleteMessage(chatId, originalMessageId)));
  }

  private boolean assigningAbilities(Update update, Session session) {
    return ABILITIES.equals(update.getCallbackQuery().getMessage().getText());
  }

  private void sendEditKeyboard(Communicate communicate, Long chatId, Integer messageId) {
    EditMessageReplyMarkup sendMessage = new EditMessageReplyMarkup();
    sendMessage.setChatId(chatId);
    sendMessage.setMessageId(messageId);
    InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
    keyboardMarkup.setKeyboard(List.of(communicate.getKeyboardButtons()));
    sendMessage.setReplyMarkup(keyboardMarkup);
    sendMessage(sendMessage);
  }

  private void sendKeyboard(Communicate communicate, Long chatId, Integer messageId) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.setText(communicate.getText());
    sendMessage.setChatId(chatId);
    sendMessage.setReplyToMessageId(messageId);
    InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
    keyboardMarkup.setKeyboard(List.of(communicate.getKeyboardButtons()));
    sendMessage.setReplyMarkup(keyboardMarkup);
    sendMessage(sendMessage);
  }

  private void sendMessage(BotApiMethod message) {
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
