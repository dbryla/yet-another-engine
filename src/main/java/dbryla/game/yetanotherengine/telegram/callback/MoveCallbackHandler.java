package dbryla.game.yetanotherengine.telegram.callback;

import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.game.Action;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.game.SubjectTurn;
import dbryla.game.yetanotherengine.domain.operations.ActionData;
import dbryla.game.yetanotherengine.domain.operations.OperationType;
import dbryla.game.yetanotherengine.session.FightSession;
import dbryla.game.yetanotherengine.telegram.Commons;
import dbryla.game.yetanotherengine.telegram.SessionFactory;
import dbryla.game.yetanotherengine.telegram.TelegramClient;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
@Profile("tg")
public class MoveCallbackHandler implements CallbackHandler {

  private final Commons commons;
  private final SessionFactory sessionFactory;
  private final TelegramClient telegramClient;

  @Override
  public void execute(Callback callback) {
    FightSession session = sessionFactory.getFightSession(callback.getSessionId());
    Game game = sessionFactory.getGame(callback.getChatId());
    Position newPosition = Position.valueOf(Integer.valueOf(callback.getData()));
    if (!session.isMoving()) {
      game.moveSubject(session.getPlayerName(), newPosition);
      telegramClient.sendTextMessage(callback.getChatId(), session.getPlayerName() + " moves to " + newPosition + ".");
      if (commons.isNextUser(callback.getPlayerName(), game)) {
        session.setMoving(true);
        telegramClient.sendTextMessage(callback.getChatId(), commons.getPlayerTurnMessage(session.getSubject()));
      }
      telegramClient.deleteMessage(callback.getChatId(), callback.getMessageId());
    } else {
      SubjectTurn turn = SubjectTurn.of(
          new Action(callback.getPlayerName(), List.of(), OperationType.MOVE, new ActionData(newPosition)));
      commons.executeTurnAndDeleteMessage(game, session, turn, callback.getChatId(), callback.getMessageId());
    }
  }
}
