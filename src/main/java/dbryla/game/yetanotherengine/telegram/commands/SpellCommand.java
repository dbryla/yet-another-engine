package dbryla.game.yetanotherengine.telegram.commands;

import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.session.Session;
import dbryla.game.yetanotherengine.telegram.Communicate;
import dbryla.game.yetanotherengine.telegram.FightFactory;
import dbryla.game.yetanotherengine.telegram.SessionFactory;
import dbryla.game.yetanotherengine.telegram.TelegramClient;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static dbryla.game.yetanotherengine.telegram.TelegramHelpers.getSessionId;
import static dbryla.game.yetanotherengine.telegram.TelegramHelpers.isNextUser;

@Component
@AllArgsConstructor
@Profile("tg")
public class SpellCommand {
  private final SessionFactory sessionFactory;
  private final FightFactory fightFactory;
  private final TelegramClient telegramClient;

  public void execute(Update update) {
    Long chatId = update.getMessage().getChatId();
    Game game = sessionFactory.getGame(chatId);
    Session session = sessionFactory.getSession(getSessionId(update.getMessage(), update.getMessage().getFrom()));
    String playerName = session.getPlayerName();
    if (game.isStarted() && !game.isEnded() && isNextUser(playerName, game)) {
      Communicate communicate = fightFactory.spellCommunicate(session.getSubject());
      session.setSpellCasting(true);
      telegramClient.sendReplyKeyboard(communicate, chatId, update.getMessage().getMessageId());
    }
  }
}
