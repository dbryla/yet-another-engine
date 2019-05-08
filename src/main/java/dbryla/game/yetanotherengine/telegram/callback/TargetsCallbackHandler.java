package dbryla.game.yetanotherengine.telegram.callback;

import dbryla.game.yetanotherengine.domain.game.Action;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.game.SubjectTurn;
import dbryla.game.yetanotherengine.domain.operations.ActionData;
import dbryla.game.yetanotherengine.domain.operations.OperationType;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.session.Session;
import dbryla.game.yetanotherengine.telegram.Commons;
import dbryla.game.yetanotherengine.telegram.Communicate;
import dbryla.game.yetanotherengine.telegram.FightFactory;
import dbryla.game.yetanotherengine.telegram.SessionFactory;
import dbryla.game.yetanotherengine.telegram.TelegramClient;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Profile("tg")
public class TargetsCallbackHandler implements CallbackHandler {

  private final Commons commons;
  private final SessionFactory sessionFactory;
  private final FightFactory fightFactory;
  private final TelegramClient telegramClient;

  @Override
  public void execute(Callback callback) {
    Session session = sessionFactory.getSession(callback.getSessionId());
    session.addTarget(callback.getData());
    Game game = sessionFactory.getGame(callback.getChatId());
    if (session.isSpellCasting()) {
      handleSpellOnTarget(session, callback.getPlayerName(), callback.getMessageId(), callback.getChatId(), game);
    } else {
      SubjectTurn turn = SubjectTurn.of(new Action(callback.getPlayerName(), callback.getData(), OperationType.ATTACK,
          new ActionData(session.getWeapon())));
      commons.executeTurnAndDeleteMessage(game, session, turn, callback.getChatId(), callback.getMessageId());
    }
  }

  private void handleSpellOnTarget(Session session, String playerName, Integer messageId, Long chatId, Game game) {
    Spell spell = session.getSpell();
    if (session.areAllTargetsAcquired()) {
      SubjectTurn turn = SubjectTurn.of(new Action(playerName, session.getTargets(), OperationType.SPELL_CAST, new ActionData(spell)));
      commons.executeTurnAndDeleteMessage(game, session, turn, chatId, messageId);
    } else {
      Optional<Communicate> communicate = fightFactory.targetCommunicate(game, playerName, spell, session.getTargets());
      communicate.ifPresent(value -> telegramClient.sendEditKeyboard(value, chatId, messageId));
    }
  }
}
