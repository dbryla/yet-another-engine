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

  private final CallbackHandlerFactory callbackHandlerFactory;
  private final Commons commons;

  public void execute(Update update) {
    Callback callback = new Callback(
        update.getCallbackQuery().getMessage().getMessageId(),
        commons.getCharacterName(update.getCallbackQuery().getFrom()),
        update.getCallbackQuery().getMessage().getChatId(),
        commons.getSessionId(update.getCallbackQuery().getMessage(), update.getCallbackQuery().getFrom()),
        update.getCallbackQuery().getData(),
        commons.getOriginalMessageId(update));
    String messageText = update.getCallbackQuery().getMessage().getText();
    log.trace("[{}] Callback: {}:{} Session: {} Player: {}",
        callback.getMessageId(),
        messageText,
        callback.getData(),
        callback.getSessionId(),
        callback.getPlayerName());
    if (!callback.getPlayerName().equals(commons.getCharacterName(update.getCallbackQuery().getMessage().getReplyToMessage().getFrom()))) {
      log.trace("[{}] Aborting handling callback. Not the owner of original command.", callback.getMessageId());
      return;
    }
    callbackHandlerFactory.getCallbackHandler(messageText).execute(callback);
  }
}
