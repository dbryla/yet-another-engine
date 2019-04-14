package dbryla.game.yetanotherengine.telegram.commands;

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
import java.util.stream.Collectors;

import static dbryla.game.yetanotherengine.domain.game.GameOptions.PLAYERS;
import static dbryla.game.yetanotherengine.domain.game.GameOptions.ENEMIES;

@Component
@AllArgsConstructor
@Profile("tg")
public class StatusCommand {
  private final SessionFactory sessionFactory;
  private final TelegramClient telegramClient;

  public void execute(Update update) {
    Long chatId = update.getMessage().getChatId();
    Game game = sessionFactory.getGame(chatId);
    if (game != null) {
      StringBuilder sb = new StringBuilder("Fight status:\n");
      Map<String, List<Subject>> subjects = game.getAllSubjects().stream().collect(Collectors.groupingBy(Subject::getAffiliation));
      statusText(sb, subjects, PLAYERS, "Your team:\n");
      statusText(sb, subjects, ENEMIES, "\nEnemies:\n");
      telegramClient.sendTextMessage(chatId, sb.toString());
    } else {
      telegramClient.sendTextMessage(chatId, "No active game at this moment.");
    }
  }

  private void statusText(StringBuilder stringBuilder, Map<String, List<Subject>> subjects, String affiliation, String header) {
    if (subjects.containsKey(affiliation)) {
      stringBuilder.append(header);
      subjects.get(affiliation).forEach(subject -> stringBuilder.append(subject.getName()).append(subject.getSubjectState()).append(" "));
    }
  }
}
