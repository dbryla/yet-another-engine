package dbryla.game.yetanotherengine.telegram.commands;

import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.telegram.SessionFactory;
import dbryla.game.yetanotherengine.telegram.TelegramClient;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Map;

import static dbryla.game.yetanotherengine.domain.battleground.Position.*;

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
    Map<Position, List<Subject>> positionsMap = game.getSubjectsPositionsMap();
    displayPosition(stringBuilder, PLAYERS_BACK, positionsMap);
    displayPosition(stringBuilder, PLAYERS_FRONT, positionsMap);
    displayPosition(stringBuilder, MID, positionsMap);
    displayPosition(stringBuilder, ENEMIES_FRONT, positionsMap);
    displayPosition(stringBuilder, ENEMIES_BACK, positionsMap);

    telegramClient.sendTextMessage(chatId, stringBuilder.toString());
  }

  private void displayPosition(StringBuilder stringBuilder, Position position, Map<Position, List<Subject>> positionsMap) {
    stringBuilder.append(position.toString()).append(": ");
    positionsMap.getOrDefault(position, List.of()).forEach(subject -> stringBuilder.append(subject.getName()).append(", "));
    stringBuilder.append("\n");
  }

}
