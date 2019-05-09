package dbryla.game.yetanotherengine.telegram.callback;

import dbryla.game.yetanotherengine.domain.game.Action;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.game.SubjectTurn;
import dbryla.game.yetanotherengine.domain.operations.ActionData;
import dbryla.game.yetanotherengine.domain.operations.OperationType;
import dbryla.game.yetanotherengine.session.FightSession;
import dbryla.game.yetanotherengine.telegram.Commons;
import dbryla.game.yetanotherengine.telegram.FightFactory;
import dbryla.game.yetanotherengine.telegram.SessionFactory;
import dbryla.game.yetanotherengine.telegram.TelegramClient;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Profile("tg")
public class WeaponCallbackHandler implements CallbackHandler {

  private final Commons commons;
  private final SessionFactory sessionFactory;
  private final TelegramClient telegramClient;
  private final FightFactory fightFactory;

  @Override
  public void execute(Callback callback) {
    FightSession session = sessionFactory.getFightSession(callback.getSessionId());
    Game game = sessionFactory.getGame(callback.getChatId());
    session.setWeapon(callback.getData());
    telegramClient.deleteMessage(callback.getChatId(), callback.getMessageId());
    fightFactory.targetCommunicate(game, callback.getPlayerName(), session.getWeapon())
        .ifPresentOrElse(
            communicate -> telegramClient.sendReplyKeyboard(communicate, callback.getChatId(), callback.getOriginalMessageId()),
            () -> commons.executeTurn(game, session,
                SubjectTurn.of(new Action(callback.getPlayerName(), game.getPossibleTargets(callback.getPlayerName(), session.getWeapon()).get(0),
                    OperationType.ATTACK, new ActionData(session.getWeapon())))));
  }
}
