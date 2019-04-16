package dbryla.game.yetanotherengine.telegram.commands;

import dbryla.game.yetanotherengine.telegram.TelegramClient;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@AllArgsConstructor
@Profile("tg")
public class HelpCommand {
  private final TelegramClient telegramClient;

  public void execute(Update update) {
    Long chatId = update.getMessage().getChatId();
    telegramClient.sendTextMessage(chatId, "This is real RPG!\n" +
        "Supported commands:\n" +
        "/start - Start game\n" +
        "/create - Create character\n" +
        "/character - Display existing character\n" +
        "/join - Join to game\n" +
        "/position - Display characters positions\n" +
        "/move front/back - Move character to position front/back\n" +
        "/fight - Fight random encounter\n" +
        "/attack - Attack with your weapon\n" +
        "/spell - Cast a spell\n" +
        "/status - Show status of fight\n" +
        "/reset - Reset game\n" +
        "/help - This manual");
  }
}
