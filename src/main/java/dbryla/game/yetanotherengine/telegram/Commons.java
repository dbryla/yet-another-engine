package dbryla.game.yetanotherengine.telegram;

import dbryla.game.yetanotherengine.domain.game.Action;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.game.SubjectTurn;
import dbryla.game.yetanotherengine.domain.operations.OperationType;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.session.Session;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static dbryla.game.yetanotherengine.domain.effects.Effect.PRONE;

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

  public void executeTurnAndDeleteMessage(Game game, Session session, SubjectTurn turn, Long chatId, Integer messageId) {
    executeTurn(game, session, turn);
    telegramClient.deleteMessage(chatId, messageId);
  }

  public void executeTurn(Game game, Session session, SubjectTurn turn) {
    if (session.isStandingUp()) {
      turn.getActions().add(0, new Action(turn.getOwnerName(), OperationType.STAND_UP));
    }
    game.execute(turn);
    session.cleanUpCallbackData();
  }

  public String getPlayerTurnMessage(Subject subject) {
    return subject.getName()
        + " what do you want to do next: "
        + getMoveOrStandUpCommand(subject)
        + " /pass /attack"
        + getSpellCommandIfApplicable(subject);
  }

  public String getPlayerTurnMessageAfterStandUp(Subject subject) {
    return subject.getName()
        + " what do you want to do next: /move /pass /attack"
        + getSpellCommandIfApplicable(subject);
  }

  private String getSpellCommandIfApplicable(Subject subject) {
    return subject.isSpellCaster() ? " or /spell" : "";
  }

  private String getMoveOrStandUpCommand(Subject subject) {
    return subject.getConditions().stream().anyMatch(condition -> PRONE.equals(condition.getEffect())) ? "/standup" : "/move";
  }

  public Integer getOriginalMessageId(Update update) {
    return update.getCallbackQuery().getMessage().getReplyToMessage().getMessageId();
  }

}
