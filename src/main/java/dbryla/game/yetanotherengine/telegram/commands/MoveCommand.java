package dbryla.game.yetanotherengine.telegram.commands;

import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.session.Session;
import dbryla.game.yetanotherengine.telegram.SessionFactory;
import dbryla.game.yetanotherengine.telegram.TelegramClient;
import dbryla.game.yetanotherengine.telegram.TelegramHelpers;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@AllArgsConstructor
@Profile("tg")
public class MoveCommand {

  private final SessionFactory sessionFactory;
  private final TelegramClient telegramClient;

  public void execute(Update update) {
    Long chatId = update.getMessage().getChatId();
    String messageText = update.getMessage().getText();
    String[] commandArguments = messageText.split(" ");
    if (commandArguments.length < 2 || commandArguments[1].isEmpty()) {
      telegramClient.sendTextMessage(chatId, "Position argument is required. Try `/move front` or `/move back`.");
      return;
    }
    Game game = sessionFactory.getGame(chatId);
    String sessionId = TelegramHelpers.getSessionId(update.getMessage(), update.getMessage().getFrom());
    Session session = sessionFactory.getSession(sessionId);
    if (game == null || game.isStarted() || session == null) {
      telegramClient.sendTextMessage(chatId, "Move command can be only executed after joining game and before fight started.");
      return;
    }
    Subject subject = game.getSubject(session.getPlayerName());
    Position newPosition = getNewPosition(commandArguments[1]);
    if (!newPosition.equals(subject.getPosition())) {
      game.moveSubject(session.getPlayerName(), newPosition);
      telegramClient.sendTextMessage(chatId, session.getPlayerName() + " moves to " + newPosition + ".");
    }
  }

  private Position getNewPosition(String commandArgument) {
    if ("FRONT".equalsIgnoreCase(commandArgument)) {
      return Position.PLAYERS_FRONT;
    } else {
      return Position.PLAYERS_BACK;
    }
  }

}
