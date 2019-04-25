package dbryla.game.yetanotherengine.telegram.commands;

import dbryla.game.yetanotherengine.telegram.Commons;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@AllArgsConstructor
@Profile("tg")
public class CreateCommand {

  private final JoinCommand joinCommand;
  private final Commons commons;

  public void execute(Update update) {
    Message message = update.getMessage();
    String playerName = commons.getCharacterName(message.getFrom());
    String sessionId = commons.getSessionId(message, message.getFrom());
    joinCommand.createNewSessionAndCharacter(message, playerName, sessionId);
  }
}
