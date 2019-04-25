package dbryla.game.yetanotherengine.telegram.commands;

import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.session.Session;
import dbryla.game.yetanotherengine.telegram.*;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@AllArgsConstructor
@Profile("tg")
public class SpellCommand {

  private final SessionFactory sessionFactory;
  private final FightFactory fightFactory;
  private final TelegramClient telegramClient;
  private final Commons commons;

  public void execute(Update update) {
    Long chatId = update.getMessage().getChatId();
    Game game = sessionFactory.getGame(chatId);
    Session session = sessionFactory.getSession(commons.getSessionId(update.getMessage(), update.getMessage().getFrom()));
    String playerName = session.getPlayerName();
    if (game.isStarted() && !game.isEnded() && commons.isNextUser(playerName, game)) {
      Communicate communicate = fightFactory.spellCommunicate(game, game.getSubject(playerName));
      if (communicate.getKeyboardButtons().isEmpty() || communicate.getKeyboardButtons().get(0).isEmpty()) {
        telegramClient.sendTextMessage(chatId, "No targets available within your range.");
      } else {
        session.setSpellCasting(true);
        telegramClient.sendReplyKeyboard(communicate, chatId, update.getMessage().getMessageId());
      }
    }
  }
}
