package dbryla.game.yetanotherengine.telegram;

import dbryla.game.yetanotherengine.telegram.callback.CallbackService;
import dbryla.game.yetanotherengine.telegram.commands.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Primary
@Component
@Profile("tg")
@Slf4j
@AllArgsConstructor
public class YetAnotherGameBot extends TelegramLongPollingBot {

  private static final String START_COMMAND = "/start";
  private static final String JOIN_COMMAND = "/join";
  private static final String HELP_COMMAND = "/help";
  private static final String FIGHT_COMMAND = "/fight";
  private static final String ATTACK_COMMAND = "/attack";
  private static final String SPELL_COMMAND = "/spell";
  private static final String STATUS_COMMAND = "/status";
  private static final String RESET_COMMAND = "/reset";
  private static final String CREATE_COMMAND = "/create";
  private static final String CHARACTER_COMMAND = "/character";
  private static final String POSITION_COMMAND = "/position";
  private static final String MOVE_COMMAND = "/move";
  private static final String PASS_COMMAND = "/pass";
  private static final String STANDUP_COMMAND = "/standup";

  private final TelegramConfig telegramConfig;
  private final CallbackService callbackService;
  private final Commons commons;

  private final StartCommand startCommand;
  private final JoinCommand joinCommand;
  private final HelpCommand helpCommand;
  private final FightCommand fightCommand;
  private final AttackCommand attackCommand;
  private final SpellCommand spellCommand;
  private final StatusCommand statusCommand;
  private final ResetCommand resetCommand;
  private final CreateCommand createCommand;
  private final CharacterCommand characterCommand;
  private final PositionCommand positionCommand;
  private final MoveCommand moveCommand;
  private final PassCommand passCommand;
  private final StandUpCommand standUpCommand;


  @Override
  public void onUpdateReceived(Update update) {
    if (isCommand(update)) {
      String commandText = update.getMessage().getText();
      handleCommand(update, commandText);
      return;
    }
    if (isCallback(update)) {
      callbackService.execute(update);
      return;
    }
    if (isRegularMessage(update)) {
      log.trace("[{}] Message: {} Session: {} Player: {}",
          update.getMessage().getMessageId(),
          update.getMessage().getText(),
          commons.getSessionId(update.getMessage(), update.getMessage().getFrom()),
          commons.getCharacterName(update.getMessage().getFrom()));
    }
  }

  private void handleCommand(Update update, String commandText) {
    log.trace("[{}] Command: {} Session: {} Player: {}",
        update.getMessage().getMessageId(),
        commandText,
        commons.getSessionId(update.getMessage(), update.getMessage().getFrom()),
        commons.getCharacterName(update.getMessage().getFrom()));
    if (commandText.startsWith(START_COMMAND)) {
      startCommand.execute(update);
    }
    if (commandText.startsWith(JOIN_COMMAND)) {
      joinCommand.execute(update);
    }
    if (commandText.startsWith(HELP_COMMAND)) {
      helpCommand.execute(update);
    }
    if (commandText.startsWith(FIGHT_COMMAND)) {
      fightCommand.execute(update);
    }
    if (commandText.startsWith(ATTACK_COMMAND)) {
      attackCommand.execute(update);
    }
    if (commandText.startsWith(SPELL_COMMAND)) {
      spellCommand.execute(update);
    }
    if (commandText.startsWith(STATUS_COMMAND)) {
      statusCommand.execute(update);
    }
    if (commandText.startsWith(RESET_COMMAND)) {
      resetCommand.execute(update);
    }
    if (commandText.startsWith(CREATE_COMMAND)) {
      createCommand.execute(update);
    }
    if (commandText.startsWith(CHARACTER_COMMAND)) {
      characterCommand.execute(update);
    }
    if (commandText.startsWith(POSITION_COMMAND)) {
      positionCommand.execute(update);
    }
    if (commandText.startsWith(MOVE_COMMAND)) {
      moveCommand.execute(update);
    }
    if (commandText.startsWith(PASS_COMMAND)) {
      passCommand.execute(update);
    }
    if (commandText.startsWith(STANDUP_COMMAND)) {
      standUpCommand.execute(update);
    }
  }

  private boolean isCommand(Update update) {
    return update.hasMessage() && update.getMessage().isCommand();
  }

  private boolean isCallback(Update update) {
    return update.hasCallbackQuery();
  }

  private boolean isRegularMessage(Update update) {
    return update.hasMessage() && update.getMessage().hasText();
  }

  @Override
  public String getBotUsername() {
    return "YetAnotherGameBot";
  }

  @Override
  public String getBotToken() {
    return telegramConfig.getToken();
  }
}
