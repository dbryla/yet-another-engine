package dbryla.game.yetanotherengine.telegram.commands;

import static dbryla.game.yetanotherengine.domain.battleground.Position.ENEMIES_BACK;
import static dbryla.game.yetanotherengine.domain.battleground.Position.ENEMIES_FRONT;
import static dbryla.game.yetanotherengine.domain.battleground.Position.MID;
import static dbryla.game.yetanotherengine.domain.battleground.Position.PLAYERS_BACK;
import static dbryla.game.yetanotherengine.domain.battleground.Position.PLAYERS_FRONT;

import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.telegram.SessionFactory;
import dbryla.game.yetanotherengine.telegram.TelegramClient;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@AllArgsConstructor
@Profile("tg")
public class PositionCommand {

  private final SessionFactory sessionFactory;
  private final TelegramClient telegramClient;

  public void execute(Update update) {
    Long chatId = update.getMessage().getChatId();
    Game game = sessionFactory.getGame(chatId);
    if (game == null) {
      telegramClient.sendTextMessage(chatId, "Please start game first.");
      return;
    }

    StringBuilder stringBuilder = new StringBuilder("Battleground:\n");
    displayPosition(game, stringBuilder, PLAYERS_BACK);
    displayPosition(game, stringBuilder, PLAYERS_FRONT);
    displayPosition(game, stringBuilder, MID);
    displayPosition(game, stringBuilder, ENEMIES_FRONT);
    displayPosition(game, stringBuilder, ENEMIES_BACK);

    telegramClient.sendTextMessage(chatId, stringBuilder.toString());
  }

  private void displayPosition(Game game, StringBuilder stringBuilder, Position position) {
    stringBuilder.append(position.toString()).append(": ");
    game.getSubjectsPositionsMap().getOrDefault(position, List.of()).forEach(subject -> stringBuilder.append(subject.getName()).append(", "));
    stringBuilder.append("\n");
  }

}
