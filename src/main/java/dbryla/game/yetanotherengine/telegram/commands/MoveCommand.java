package dbryla.game.yetanotherengine.telegram.commands;

import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.session.Session;
import dbryla.game.yetanotherengine.telegram.FightFactory;
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
  private final FightFactory fightFactory;

  public void execute(Update update) {
    Long chatId = update.getMessage().getChatId();
    Game game = sessionFactory.getGame(chatId);
    String sessionId = TelegramHelpers.getSessionId(update.getMessage(), update.getMessage().getFrom());
    Session session = sessionFactory.getSession(sessionId);
    if (game == null || session == null) {
      telegramClient.sendTextMessage(chatId, "Move command can be only executed after joining game.");
      return;
    }
    Subject subject = game.getSubject(session.getPlayerName());
    telegramClient
        .sendReplyKeyboard(fightFactory.moveCommunicate(subject, game), chatId, update.getMessage().getMessageId());
  }

}
