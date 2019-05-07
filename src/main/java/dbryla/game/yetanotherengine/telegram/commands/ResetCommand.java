package dbryla.game.yetanotherengine.telegram.commands;

import dbryla.game.yetanotherengine.telegram.session.GameStorage;
import dbryla.game.yetanotherengine.telegram.TelegramClient;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@AllArgsConstructor
@Profile("tg")
public class ResetCommand {

  private final GameStorage gameStorage;
  private final TelegramClient telegramClient;

  public void execute(Update update) {
    Long chatId = update.getMessage().getChatId();
    gameStorage.clearGame(chatId);
    telegramClient.sendTextMessage(chatId, "Ok :) I'll forget anything that happened.");
  }
}
