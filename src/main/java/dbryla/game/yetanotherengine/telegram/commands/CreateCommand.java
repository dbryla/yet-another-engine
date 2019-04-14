package dbryla.game.yetanotherengine.telegram.commands;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static dbryla.game.yetanotherengine.telegram.TelegramHelpers.getCharacterName;
import static dbryla.game.yetanotherengine.telegram.TelegramHelpers.getSessionId;

@Component
@AllArgsConstructor
@Profile("tg")
public class CreateCommand {

  private final JoinCommand joinCommand;

  public void execute(Update update) {
    Message message = update.getMessage();
    String playerName = getCharacterName(message.getFrom());
    String sessionId = getSessionId(message, message.getFrom());
    joinCommand.createNewSessionAndCharacter(message, playerName, sessionId);
  }
}
