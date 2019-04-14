package dbryla.game.yetanotherengine.telegram.commands;

import dbryla.game.yetanotherengine.telegram.SessionFactory;
import dbryla.game.yetanotherengine.telegram.TelegramClient;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@AllArgsConstructor
@Profile("tg")
public class StartCommand {
  private final SessionFactory sessionFactory;
  private final TelegramClient telegramClient;

  private static final String START_GAME = "Starting new game.";

  public void execute(Update update) {
    Long chatId = update.getMessage().getChatId();
    sessionFactory.getGameOrCreate(chatId);
    telegramClient.sendTextMessage(chatId, "Welcome to real RPG feeling! " + START_GAME);
  }
}
