package dbryla.game.yetanotherengine.telegram;

import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

public class TelegramHelpers {

  public static String getSessionId(Message message, User user) {
    return message.getChatId() + "/" + user.getId();
  }

  public static String getCharacterName(User from) {
    return from.getFirstName();
  }

  public static boolean isNextUser(String playerName, Game game) {
    return game.getNextSubjectName().isPresent() && playerName.equals(game.getNextSubjectName().get());
  }

  public static String getSpellCommandIfApplicable(Subject subject) {
    return subject.isSpellCaster() ? " or /spell" : "";
  }

}
