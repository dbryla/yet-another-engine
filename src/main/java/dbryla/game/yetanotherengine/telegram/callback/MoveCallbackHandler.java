package dbryla.game.yetanotherengine.telegram.callback;

import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.game.Action;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.game.SubjectTurn;
import dbryla.game.yetanotherengine.domain.operations.ActionData;
import dbryla.game.yetanotherengine.domain.operations.OperationType;
import dbryla.game.yetanotherengine.session.Session;
import dbryla.game.yetanotherengine.telegram.Commons;
import dbryla.game.yetanotherengine.telegram.SessionFactory;
import dbryla.game.yetanotherengine.telegram.TelegramClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@AllArgsConstructor
@Component
public class MoveCallbackHandler implements CallbackHandler {

  private final Commons commons;
  private final SessionFactory sessionFactory;
  private final TelegramClient telegramClient;

  @Override
  public void execute(Update update) {
    Long chatId = update.getCallbackQuery().getMessage().getChatId();
    Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
    String sessionId = commons.getSessionId(update.getCallbackQuery().getMessage(), update.getCallbackQuery().getFrom());
    Session session = sessionFactory.getSession(sessionId);
    Game game = sessionFactory.getGame(chatId);
    String playerName = commons.getCharacterName(update.getCallbackQuery().getFrom());
    String callbackData = update.getCallbackQuery().getData();
    if (!session.isMoving()) {
      Position newPosition = Position.valueOf(Integer.valueOf(callbackData));
      game.moveSubject(session.getPlayerName(), newPosition);
      telegramClient.sendTextMessage(chatId, session.getPlayerName() + " moves to " + newPosition + ".");
      if (commons.isNextUser(playerName, game)) {
        session.setMoving(true);
        telegramClient.sendTextMessage(chatId, commons.getPlayerTurnMessage(session.getSubject()));
      }
      telegramClient.deleteMessage(chatId, messageId);
    } else {
      SubjectTurn turn = SubjectTurn.of(
          new Action(playerName, List.of(), OperationType.MOVE, new ActionData(Position.valueOf(Integer.valueOf(callbackData)))));
      commons.executeTurnAndDeleteMessage(game, session, turn, chatId, messageId);
    }
  }
}
