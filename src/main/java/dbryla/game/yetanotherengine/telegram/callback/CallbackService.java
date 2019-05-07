package dbryla.game.yetanotherengine.telegram.callback;

import dbryla.game.yetanotherengine.telegram.Commons;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Slf4j
@AllArgsConstructor
@Profile("tg")
public class CallbackService {

  private final CallbackFactory callbackFactory;
  private final Commons commons;

  public void execute(Update update) {
    String messageText = update.getCallbackQuery().getMessage().getText();
    Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
    String sessionId = commons.getSessionId(update.getCallbackQuery().getMessage(), update.getCallbackQuery().getFrom());
    log.trace("Callback: {} no:{} [{}]", messageText, messageId, sessionId);
    String playerName = commons.getCharacterName(update.getCallbackQuery().getFrom());
    if (!playerName.equals(commons.getCharacterName(update.getCallbackQuery().getMessage().getReplyToMessage().getFrom()))) {
      log.trace("Aborting handling callback. Not the owner of original command.");
      return;
    }
    callbackFactory.getCallbackHandler(messageText).execute(update);
  }
}
