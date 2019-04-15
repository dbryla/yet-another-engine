package dbryla.game.yetanotherengine.telegram.commands;

import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.telegram.SessionFactory;
import dbryla.game.yetanotherengine.telegram.TelegramClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@AllArgsConstructor
public class PositionCommand {

  private final SessionFactory sessionFactory;
  private final TelegramClient telegramClient;

  public void execute(Update update) {
    Long chatId = update.getMessage().getChatId();
    Game game = sessionFactory.getGame(chatId);
    StringBuilder stringBuilder = new StringBuilder("Battleground:\n");
    game.getSubjectsPositionsMap().forEach(
        (position, subjects) -> {
          stringBuilder.append(position.toString()).append(": ");
          subjects.forEach(subject -> stringBuilder.append(subject).append(","));
          stringBuilder.append("\n");
        }
    );
    telegramClient.sendTextMessage(chatId, stringBuilder.toString());
  }

}
