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
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
@Profile("tg")
public class SpellCallbackHandler implements CallbackHandler {

  private final TelegramClient telegramClient;
  private final Commons commons;
  private final SessionFactory sessionFactory;
  private final FightFactory fightFactory;

  @Override
  public void execute(Callback callback) {
    Session session = sessionFactory.getSession(callback.getSessionId());
    Spell spell = Spell.valueOf(callback.getData());
    telegramClient.deleteMessage(callback.getChatId(), callback.getMessageId());
    Game game = sessionFactory.getGame(callback.getChatId());
    List<String> possibleTargets = game.getPossibleTargets(callback.getPlayerName(), spell);
    if (!spell.isAreaOfEffectSpell() && possibleTargets.size() > spell.getMaximumNumberOfTargets()) {
      Optional<Communicate> communicate = fightFactory.targetCommunicate(possibleTargets);
      if (communicate.isPresent()) {
        telegramClient.sendReplyKeyboard(communicate.get(), callback.getChatId(), callback.getOriginalMessageId());
        return;
      }
    }
    SubjectTurn turn = SubjectTurn.of(new Action(callback.getPlayerName(), possibleTargets, OperationType.SPELL_CAST, new ActionData(spell)));
    commons.executeTurn(game, session, turn);
  }
}
