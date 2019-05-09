package dbryla.game.yetanotherengine.telegram.commands;

import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.session.FightSession;
import dbryla.game.yetanotherengine.telegram.FightFactory;
import dbryla.game.yetanotherengine.telegram.SessionFactory;
import dbryla.game.yetanotherengine.telegram.TelegramClient;
import dbryla.game.yetanotherengine.telegram.Commons;
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
  private final Commons commons;

  public void execute(Update update) {
    Long chatId = update.getMessage().getChatId();
    Game game = sessionFactory.getGame(chatId);
    String sessionId = commons.getSessionId(update.getMessage(), update.getMessage().getFrom());
    FightSession session = sessionFactory.getFightSession(sessionId);
    if (game == null || session == null) {
      telegramClient.sendTextMessage(chatId, "Move command can be only executed after joining game.");
      return;
    }
    Subject subject = game.getSubject(session.getPlayerName());
    if (subject == null) {
      telegramClient.sendTextMessage(chatId, "Please create character and join game first.");
      return;
    }
    fightFactory.moveCommunicate(game, subject)
        .ifPresentOrElse(communicate -> telegramClient.sendReplyKeyboard(communicate, chatId, update.getMessage().getMessageId()),
            () -> telegramClient.sendTextMessage(chatId, "You are unable to move right now."));
  }

}
