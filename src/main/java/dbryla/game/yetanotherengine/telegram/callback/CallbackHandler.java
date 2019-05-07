package dbryla.game.yetanotherengine.telegram.callback;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CallbackHandler {
  void execute(Update update);
}
