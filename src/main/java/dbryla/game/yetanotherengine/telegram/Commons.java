package dbryla.game.yetanotherengine.telegram;

import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.game.SubjectTurn;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.session.Session;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
@AllArgsConstructor
public class Commons {

  private final TelegramClient telegramClient;

  public String getSessionId(Message message, User user) {
    return message.getChatId() + "/" + user.getId();
  }

  public String getCharacterName(User from) {
    return from.getFirstName();
  }

  public boolean isNextUser(String playerName, Game game) {
    return game != null && game.getNextSubjectName().isPresent() && playerName.equals(game.getNextSubjectName().get());
  }

  public void executeTurn(Game game, Session session, SubjectTurn turn, Long chatId, Integer messageId) {
    if (messageId != null) {
      telegramClient.deleteMessage(chatId, messageId);
    }
    game.execute(turn);
    session.cleanUpCallbackData();
  }

  String getPlayerTurnMessage(Subject subject) {
    return subject.getName() + " what do you want to do next: /move /pass /attack" + getSpellCommandIfApplicable(subject);
  }

  private String getSpellCommandIfApplicable(Subject subject) {
    return subject.isSpellCaster() ? " or /spell" : "";
  }
}
