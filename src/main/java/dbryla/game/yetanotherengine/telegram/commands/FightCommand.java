package dbryla.game.yetanotherengine.telegram.commands;

import dbryla.game.yetanotherengine.domain.encounters.MonstersFactory;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.telegram.SessionFactory;
import dbryla.game.yetanotherengine.telegram.TelegramClient;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@AllArgsConstructor
@Profile("tg")
public class FightCommand {
  private final SessionFactory sessionFactory;
  private final TelegramClient telegramClient;
  private final MonstersFactory monstersFactory;

  public void execute(Update update) {
    Long chatId = update.getMessage().getChatId();
    Game game = sessionFactory.getGame(chatId);
    if (game == null) {
      telegramClient.sendTextMessage(chatId, "Please start game first!");
      return;
    }
    if (game.isStarted()) {
      telegramClient.sendTextMessage(chatId, "Game already started!");
      return;
    }
    int playersNumber = game.getPlayersNumber();
    if (playersNumber == 0) {
      telegramClient.sendTextMessage(chatId, "Please join game before starting fight!");
      return;
    }
    game.createEnemies(monstersFactory.createEncounter(playersNumber));
    game.start();
  }
}
