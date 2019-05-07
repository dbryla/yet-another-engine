package dbryla.game.yetanotherengine.telegram.callback;

import dbryla.game.yetanotherengine.domain.game.Action;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.game.SubjectTurn;
import dbryla.game.yetanotherengine.domain.operations.ActionData;
import dbryla.game.yetanotherengine.domain.operations.OperationType;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.session.Session;
import dbryla.game.yetanotherengine.telegram.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

import static dbryla.game.yetanotherengine.telegram.CommunicateText.SPELL;

@Component
@AllArgsConstructor
public class TargetsCallbackHandler implements CallbackHandler {

  private final Commons commons;
  private final SessionFactory sessionFactory;
  private final FightFactory fightFactory;
  private final TelegramClient telegramClient;

  @Override
  public void execute(Update update) {Long chatId = update.getCallbackQuery().getMessage().getChatId();
    Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
    String sessionId = commons.getSessionId(update.getCallbackQuery().getMessage(), update.getCallbackQuery().getFrom());
    Session session = sessionFactory.getSession(sessionId);
    String callbackData = update.getCallbackQuery().getData();
    String playerName = commons.getCharacterName(update.getCallbackQuery().getFrom());
    Game game = sessionFactory.getGame(chatId);
    if (session.isSpellCasting()) {
      handleSpellOnTarget(session, playerName, messageId, chatId, game);
    } else {
      SubjectTurn turn = SubjectTurn.of(new Action(playerName, callbackData, OperationType.ATTACK, new ActionData(session.getWeapon())));
      commons.executeTurnAndDeleteMessage(game, session, turn, chatId, messageId);
    }
  }

  private void handleSpellOnTarget(Session session, String playerName, Integer messageId, Long chatId, Game game) {
    Spell spell = Spell.valueOf((String) session.getGenericData().get(SPELL));
    if (session.areAllTargetsAcquired()) {
      SubjectTurn turn = SubjectTurn.of(new Action(playerName, session.getTargets(), OperationType.SPELL_CAST, new ActionData(spell)));
      commons.executeTurnAndDeleteMessage(game, session, turn, chatId, messageId);
    } else {
      Optional<Communicate> communicate = fightFactory.targetCommunicate(game, playerName, spell, session.getTargets());
      communicate.ifPresent(value -> telegramClient.sendEditKeyboard(value, chatId, messageId));
    }
  }
}
