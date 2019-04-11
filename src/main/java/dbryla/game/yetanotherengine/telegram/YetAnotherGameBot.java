package dbryla.game.yetanotherengine.telegram;

import static dbryla.game.yetanotherengine.telegram.CommunicateFactory.ABILITIES;
import static dbryla.game.yetanotherengine.telegram.CommunicateFactory.CLASS;

import dbryla.game.yetanotherengine.domain.AbilityScoresSupplier;
import dbryla.game.yetanotherengine.domain.Game;
import dbryla.game.yetanotherengine.domain.GameFactory;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.session.Session;
import dbryla.game.yetanotherengine.session.SessionStorage;
import java.util.LinkedList;
import java.util.List;
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

@Component
@Profile("tg")
@Slf4j
@AllArgsConstructor
public class YetAnotherGameBot extends TelegramLongPollingBot {

  private static final String START_COMMAND = "/start";
  private static final String JOIN_COMMAND = "/join";
  private static final String START_GAME = "Starting new game.";
  private static final String HELP_COMMAND = "/help";
  private static final String FIGHT_COMMAND = "/fight";

  private final CommunicateFactory communicateFactory;
  private final SessionStorage sessionStorage;
  private final AbilityScoresSupplier abilityScoresSupplier;
  private final GameFactory gameFactory;
  private final CharacterBuilder characterBuilder;

  @Override
  public void onUpdateReceived(Update update) {
    if (isCommand(update)) {
      log.trace("Command: {} no:{} [{}]", update.getMessage().getText(), update.getMessage().getMessageId(),
          getSessionId(update.getMessage(), update.getMessage().getFrom()));
      String commandText = update.getMessage().getText();
      if (commandText.startsWith(START_COMMAND)) {
        handleStartCommand(update);
      }
      if (commandText.startsWith(JOIN_COMMAND)) {
        handleJoinCommand(update);
      }
      if (commandText.startsWith(HELP_COMMAND)) {
        handleHelpCommand(update);
      }
      if (commandText.startsWith(FIGHT_COMMAND)) {
        handleFightCommand(update);
      }
      return;
    }
    if (isCallback(update)) {
      log.trace("Callback: {} no:{} [{}]", update.getCallbackQuery().getMessage().getText(),
          update.getCallbackQuery().getMessage().getMessageId(),
          getSessionId(update.getCallbackQuery().getMessage(),
              update.getCallbackQuery().getFrom()));
      handleCallback(update);
      return;
    }
    if (isRegularMessage(update)) {
      log.trace("Update: {} no:{} [{}]", update.getMessage().getText(), update.getMessage().getMessageId(),
          getSessionId(update.getMessage(), update.getMessage().getFrom()));
    }

  }

  private boolean isCommand(Update update) {
    return update.hasMessage() && update.getMessage().isCommand();
  }

  private void handleStartCommand(Update update) {
    Long chatId = update.getMessage().getChatId();
    sessionStorage.put(chatId, gameFactory.newGame());
    sendTextMessage(chatId, "Welcome to real RPG feeling! " + START_GAME);
  }

  private void handleHelpCommand(Update update) {
    Long chatId = update.getMessage().getChatId();
    sendTextMessage(chatId, "This is real RPG!\nSupported commands:\n/start - Start game\n/join - Joins you to game"
        + "\nfight - Fight random encounter\n/help - This manual");
  }

  private void handleFightCommand(Update update) {

  }

  private boolean isCallback(Update update) {
    return update.hasCallbackQuery();
  }

  private boolean isRegularMessage(Update update) {
    return update.hasMessage() && update.getMessage().hasText();
  }

  private void sendTextMessage(Long chatId, String text) {
    sendMessage(new SendMessage()
        .setChatId(chatId)
        .setText(text));
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
    String messageText = update.getCallbackQuery().getMessage().getText();
    session.update(messageText, callbackData);
    Integer originalMessageId = update.getCallbackQuery().getMessage().getMessageId();
    Long chatId = update.getCallbackQuery().getMessage().getChatId();
    if (messageText.contains(CLASS)) {
      session.addLastCommunicate(communicateFactory.chooseWeaponCommunicate(callbackData));
      communicateFactory.chooseArmorCommunicate(callbackData).ifPresent(session::addLastCommunicate);
    }
    if (assigningAbilities(messageText) && session.getAbilityScores().size() > 1) {
      session.addNextCommunicate(
          communicateFactory.nextAbilityAssignment(session, callbackData));
    }
    session.getNextCommunicate().ifPresentOrElse(communicate -> {
      if (assigningAbilities(messageText) && ABILITIES.equals(communicate.getText())) {
        sendEditKeyboard(communicate, chatId, originalMessageId);
      } else {
        sendMessage(new DeleteMessage(chatId, originalMessageId));
        sendKeyboard(communicate, chatId, session.getOriginalMessageId());
      }
    }, () -> {
      sendMessage(new DeleteMessage(chatId, originalMessageId));
      createCharacter(chatId, session);
    });
  }

  private void createCharacter(Long chatId, Session session) {
    Subject subject = characterBuilder.create(session);
    Game game = getGame(chatId);
    game.createCharacter(subject);
    sendTextMessage(chatId, session.getPlayerName() + ": Your character has been created. \n" + subject);
  }

  private Game getGame(Long chatId) {
    Game game = sessionStorage.get(chatId);
    if (game == null) {
      sessionStorage.put(chatId, gameFactory.newGame());
      game = sessionStorage.get(chatId);
    }
    return game;
  }

  private boolean assigningAbilities(String messageText) {
    return messageText.contains(ABILITIES);
  }

  private void sendEditKeyboard(Communicate communicate, Long chatId, Integer messageId) {
    EditMessageReplyMarkup sendMessage = new EditMessageReplyMarkup();
    sendMessage.setChatId(chatId);
    sendMessage.setMessageId(messageId);
    InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
    keyboardMarkup.setKeyboard(communicate.getKeyboardButtons());
    sendMessage.setReplyMarkup(keyboardMarkup);
    sendMessage(sendMessage);
  }

  private void sendKeyboard(Communicate communicate, Long chatId, Integer messageId) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.setText(communicate.getText());
    sendMessage.setChatId(chatId);
    sendMessage.setReplyToMessageId(messageId);
    InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
    keyboardMarkup.setKeyboard(communicate.getKeyboardButtons());
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

}
