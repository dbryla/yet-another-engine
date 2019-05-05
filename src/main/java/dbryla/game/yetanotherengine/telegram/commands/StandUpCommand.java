package dbryla.game.yetanotherengine.telegram.commands;

import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.session.Session;
import dbryla.game.yetanotherengine.telegram.Commons;
import dbryla.game.yetanotherengine.telegram.SessionFactory;
import dbryla.game.yetanotherengine.telegram.TelegramClient;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@AllArgsConstructor
@Profile("tg")
public class StandUpCommand {

  private final SessionFactory sessionFactory;
  private final Commons commons;
  private final TelegramClient telegramClient;

  public void execute(Update update) {
    Long chatId = update.getMessage().getChatId();
    Game game = sessionFactory.getGame(chatId);
    Session session = sessionFactory.getSession(commons.getSessionId(update.getMessage(), update.getMessage().getFrom()));
    String playerName = session.getPlayerName();
    if (game.isStarted() && !game.isEnded() && commons.isNextUser(playerName, game)) {
      session.setStandingUp(true);
      session.setMoving(true);
      telegramClient.sendTextMessage(chatId, commons.getPlayerTurnMessageAfterStandUp(session.getSubject()));
    }
  }
}
