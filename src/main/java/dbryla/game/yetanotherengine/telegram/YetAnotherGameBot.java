package dbryla.game.yetanotherengine.telegram;

import static dbryla.game.yetanotherengine.telegram.BuildingFactory.ABILITIES;
import static dbryla.game.yetanotherengine.telegram.BuildingFactory.CLASS;
import static dbryla.game.yetanotherengine.telegram.FightFactory.SPELL;
import static dbryla.game.yetanotherengine.telegram.FightFactory.TARGET;

import dbryla.game.yetanotherengine.InputProvider;
import dbryla.game.yetanotherengine.domain.Game;
import dbryla.game.yetanotherengine.domain.GameFactory;
import dbryla.game.yetanotherengine.domain.GameOptions;
import dbryla.game.yetanotherengine.domain.events.Event;
import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.events.LoggingEventHub;
import dbryla.game.yetanotherengine.domain.operations.AttackOperation;
import dbryla.game.yetanotherengine.domain.operations.Operation;
import dbryla.game.yetanotherengine.domain.operations.SpellCastOperation;
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

import java.util.Optional;

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
  private static final String SPELL_COMMAND = "/spell";

  private final SessionStorage sessionStorage;
  private final GameFactory gameFactory;
  private final SessionFactory sessionFactory;
  private final KeyboardFactory keyboardFactory;
  private final LoggingEventHub loggingEventHub;
  private final GameOptions gameOptions;
  private final AttackOperation attackOperation;
  private final SpellCastOperation spellCastOperation;
  private final FightFactory fightFactory;

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
      if (commandText.startsWith(SPELL_COMMAND)) {
        handleSpellCommand(update);
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
    Communicate communicate = session.getNextBuildingCommunicate().get();
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
    Session session = sessionStorage.get(getSessionId(update.getMessage(), update.getMessage().getFrom()));
    String playerName = session.getPlayerName();
    if (game.isStarted() && isNextUser(playerName, game)) {
      session.setSpellCasting(false);
      fightFactory.targetCommunicate(game)
          .ifPresentOrElse(
              communicate -> sendMessage(keyboardFactory
                  .replyKeyboard(communicate, chatId, update.getMessage().getMessageId())),
              () -> game.attack(playerName, attackOperation, this));
    }
  }

  private void handleSpellCommand(Update update) {
    Long chatId = update.getMessage().getChatId();
    Game game = sessionStorage.get(chatId);
    Session session = sessionStorage.get(getSessionId(update.getMessage(), update.getMessage().getFrom()));
    String playerName = session.getPlayerName();
    if (game.isStarted() && isNextUser(playerName, game)) {
      Communicate communicate = fightFactory.spellCommunicate((String) session.getData().get(CLASS));
      session.setSpellCasting(true);
      sendMessage(keyboardFactory.replyKeyboard(communicate, chatId, update.getMessage().getMessageId()));
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
    String playerName = update.getCallbackQuery().getFrom().getFirstName();
    String callbackData = update.getCallbackQuery().getData();
    String messageText = update.getCallbackQuery().getMessage().getText();
    Integer originalMessageId = update.getCallbackQuery().getMessage().getMessageId();
    Long chatId = update.getCallbackQuery().getMessage().getChatId();
    sessionFactory.updateSession(messageText, session, callbackData);
    session.getNextBuildingCommunicate()
        .ifPresentOrElse(communicate ->
                handleCharacterBuilding(communicate, session, messageText, originalMessageId, chatId),
            () -> {
              Game game = getGame(chatId);
              if (messageText.startsWith(SPELL)) {
                Optional<Communicate> communicate = fightFactory.targetCommunicate(game); //fixme not every spell needs to check that
                if (communicate.isPresent()) {
                  sendMessage(keyboardFactory.editKeyboard(communicate.get(), chatId, originalMessageId));
                  return;
                }
                game.attack(playerName, spellCastOperation, this); // fixme pass spell
              }
              sendMessage(new DeleteMessage(chatId, originalMessageId));
              if (messageText.startsWith(TARGET)) {
                game.attack(playerName, getAction(session), callbackData, this); // fixme spellcast needs an target and spell
              }
              createCharacterIfNeeded(chatId, session);
            });
  }

  private Operation getAction(Session session) {
    return session.isSpellCasting() ? spellCastOperation : attackOperation;
  }

  private void handleCharacterBuilding(Communicate communicate, Session session, String messageText, Integer originalMessageId, Long chatId) {
    if (messageText.contains(ABILITIES) && ABILITIES.equals(communicate.getText())) {
      sendMessage(keyboardFactory.editKeyboard(communicate, chatId, originalMessageId));
    } else {
      sendMessage(new DeleteMessage(chatId, originalMessageId));
      sendMessage(keyboardFactory.replyKeyboard(communicate, chatId, session.getOriginalMessageId()));
    }
  }

  private void sendMessage(BotApiMethod message) {
    try {
      execute(message);
    } catch (TelegramApiException e) {
      log.error("Error while sending message to Telegram", e);
    }
  }

  private void createCharacterIfNeeded(Long chatId, Session session) {
    if (!session.isReadyToPlay()) {
      Subject subject = sessionFactory.createCharacter(session);
      session.setReadyToPlay(true);
      Game game = getGame(chatId);
      game.createCharacter(subject);
      sendTextMessage(chatId, session.getPlayerName() + ": Your character has been created. \n" + subject);
    }
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
