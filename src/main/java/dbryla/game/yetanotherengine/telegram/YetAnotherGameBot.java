package dbryla.game.yetanotherengine.telegram;

import static dbryla.game.yetanotherengine.telegram.CommunicateFactory.ABILITIES;

import dbryla.game.yetanotherengine.InputProvider;
import dbryla.game.yetanotherengine.domain.Game;
import dbryla.game.yetanotherengine.domain.GameFactory;
import dbryla.game.yetanotherengine.domain.GameOptions;
import dbryla.game.yetanotherengine.domain.events.Event;
import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.events.LoggingEventHub;
import dbryla.game.yetanotherengine.domain.operations.AttackOperation;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.session.Session;
import dbryla.game.yetanotherengine.session.SessionStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Primary
@Component
@Profile("tg")
@Slf4j
@AllArgsConstructor
public class YetAnotherGameBot extends TelegramLongPollingBot implements InputProvider, EventHub {

  private static final String START_COMMAND = "/start";
  private static final String JOIN_COMMAND = "/join";
  private static final String START_GAME = "Starting new game.";
  private static final String HELP_COMMAND = "/help";
  private static final String FIGHT_COMMAND = "/fight";
  private static final String ATTACK_COMMAND = "/attack";

  private final SessionStorage sessionStorage;
  private final GameFactory gameFactory;
  private final CharacterBuilder characterBuilder;
  private final SessionFactory sessionFactory;
  private final KeyboardFactory keyboardFactory;
  private final LoggingEventHub loggingEventHub;
  private final GameOptions gameOptions;
  private final CommunicateFactory communicateFactory;
  private final AttackOperation attackOperation;

  @Override
  @Async
  public void send(Event event, Long gameId) {
    loggingEventHub.send(event, gameId);
    sendTextMessage(gameId, event.toString());
  }

  @Override
  @Async
  public void nextMove(Subject subject, Long gameId) {
    loggingEventHub.nextMove(subject, gameId);
    askForAction(subject, gameId);
  }

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
      if (commandText.startsWith(ATTACK_COMMAND)) {
        handleAttackCommand(update);
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

  private String getSessionId(Message message, User user) {
    return message.getChatId() + "/" + user.getId();
  }

  private void handleStartCommand(Update update) {
    Long chatId = update.getMessage().getChatId();
    sessionStorage.put(chatId, gameFactory.newGame(chatId));
    sendTextMessage(chatId, "Welcome to real RPG feeling! " + START_GAME);
  }

  void sendTextMessage(Long chatId, String text) {
    sendMessage(new SendMessage()
        .setChatId(chatId)
        .setText(text));
  }

  private void handleJoinCommand(Update update) {
    String playerName = update.getMessage().getFrom().getFirstName();
    Message message = update.getMessage();
    Session session = sessionFactory.createSession(playerName, message.getMessageId());
    sessionStorage.put(getSessionId(message, message.getFrom()), session);
    Communicate communicate = session.getNextCommunicate().get();
    sendMessage(keyboardFactory.replyKeyboard(communicate, update.getMessage().getChatId(), session.getOriginalMessageId()));
  }


  private void handleHelpCommand(Update update) {
    Long chatId = update.getMessage().getChatId();
    sendTextMessage(chatId, "This is real RPG!\nSupported commands:\n/start - Start game\n/join - Join to game"
        + "\n/fight - Fight random encounter\n/attack - Attack with your weapon\n/spell - Cast a spell\n/help - This manual");
  }

  private void handleFightCommand(Update update) {
    Long chatId = update.getMessage().getChatId();
    Game game = sessionStorage.get(chatId);
    if (!game.isStarted()) {
      game.createEnemies((int) game.getPlayersNumber());
      game.start(this);
    }
  }

  private void handleAttackCommand(Update update) {
    Long chatId = update.getMessage().getChatId();
    Game game = sessionStorage.get(chatId);
    String playerName = update.getMessage().getFrom().getFirstName();
    if (game.isStarted() && isNextUser(playerName, game)) {
      communicateFactory.attackCommunicate(game)
          .ifPresentOrElse(communicate -> sendMessage(keyboardFactory.replyKeyboard(communicate, chatId, update.getMessage().getMessageId())),
              () -> game.attack(playerName, attackOperation, this));

    }
  }

  private boolean isNextUser(String playerName, Game game) {
    return game.getNextSubjectName().isPresent() && playerName.equals(game.getNextSubjectName().get());
  }

  private boolean isCallback(Update update) {
    return update.hasCallbackQuery();
  }

  private boolean isRegularMessage(Update update) {
    return update.hasMessage() && update.getMessage().hasText();
  }

  private void handleCallback(Update update) {
    Session session = sessionStorage
        .get(getSessionId(update.getCallbackQuery().getMessage(), update.getCallbackQuery().getFrom()));
    String callbackData = update.getCallbackQuery().getData();
    String messageText = update.getCallbackQuery().getMessage().getText();
    Integer originalMessageId = update.getCallbackQuery().getMessage().getMessageId();
    Long chatId = update.getCallbackQuery().getMessage().getChatId();
    sessionFactory.updateSession(messageText, session, callbackData);
    session.getNextCommunicate().ifPresentOrElse(communicate -> {
      if (messageText.contains(ABILITIES) && ABILITIES.equals(communicate.getText())) {
        sendMessage(keyboardFactory.editKeyboard(communicate, chatId, originalMessageId));
      } else {
        sendMessage(new DeleteMessage(chatId, originalMessageId));
        sendMessage(keyboardFactory.replyKeyboard(communicate, chatId, session.getOriginalMessageId()));
      }
    }, () -> {
      sendMessage(new DeleteMessage(chatId, originalMessageId));
      createCharacter(chatId, session);
    });
  }

  private void sendMessage(BotApiMethod message) {
    try {
      execute(message);
    } catch (TelegramApiException e) {
      log.error("Error while sending message to Telegram", e);
    }
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
      sessionStorage.put(chatId, gameFactory.newGame(chatId));
      game = sessionStorage.get(chatId);
    }
    return game;
  }

  @Override
  public void askForAction(Subject subject, Long gameId) {
    sendTextMessage(gameId, subject.getName() + " your move: /attack " + getSpellCommandIfApplicable(subject));
  }

  private String getSpellCommandIfApplicable(Subject subject) {
    return gameOptions.isSpellCaster(subject.getClass().getSimpleName()) ? "or /spell" : "";
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
