package dbryla.game.yetanotherengine.telegram.commands;

import static dbryla.game.yetanotherengine.telegram.TelegramHelpers.getSessionId;

import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.session.Session;
import dbryla.game.yetanotherengine.telegram.Communicate;
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
public class AttackCommand {

  private final SessionFactory sessionFactory;
  private final TelegramClient telegramClient;
  private final FightFactory fightFactory;

  public void execute(Update update) {
    Long chatId = update.getMessage().getChatId();
    Game game = sessionFactory.getGame(chatId);
    Session session = sessionFactory.getSession(getSessionId(update.getMessage(), update.getMessage().getFrom()));
    String playerName = session.getPlayerName();
    if (game.isStarted() && !game.isEnded() && TelegramHelpers.isNextUser(playerName, game)) {
      session.setSpellCasting(false);
      Communicate communicate = fightFactory.weaponCommunicate(game, playerName);
      if (communicate.getKeyboardButtons().isEmpty() || communicate.getKeyboardButtons().get(0).isEmpty()) {
        telegramClient.sendTextMessage(chatId, "No targets available within your range.");
      } else {
        telegramClient.sendReplyKeyboard(communicate, chatId, update.getMessage().getMessageId());
      }
    }
  }
}
