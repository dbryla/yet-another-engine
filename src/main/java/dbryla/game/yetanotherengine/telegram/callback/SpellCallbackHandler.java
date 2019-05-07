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

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Component
public class SpellCallbackHandler implements CallbackHandler{

  private final TelegramClient telegramClient;
  private final Commons commons;
  private final SessionFactory sessionFactory;
  private final FightFactory fightFactory;

  @Override
  public void execute(Update update) {
    Long chatId = update.getCallbackQuery().getMessage().getChatId();
    Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
    String sessionId = commons.getSessionId(update.getCallbackQuery().getMessage(), update.getCallbackQuery().getFrom());
    Session session = sessionFactory.getSession(sessionId);
    String callbackData = update.getCallbackQuery().getData();
    Spell spell = Spell.valueOf(callbackData);
    telegramClient.deleteMessage(chatId, messageId);
    Game game = sessionFactory.getGame(chatId);
    String playerName = commons.getCharacterName(update.getCallbackQuery().getFrom());
    List<String> possibleTargets = game.getPossibleTargets(playerName, spell);
    if (!spell.isAreaOfEffectSpell() && possibleTargets.size() > spell.getMaximumNumberOfTargets()) {
      Optional<Communicate> communicate = fightFactory.targetCommunicate(possibleTargets);
      if (communicate.isPresent()) {
        telegramClient.sendReplyKeyboard(communicate.get(), chatId, commons.getOriginalMessageId(update));
        return;
      }
    }
    SubjectTurn turn = SubjectTurn.of(new Action(playerName, possibleTargets, OperationType.SPELL_CAST, new ActionData(spell)));
    commons.executeTurn(game, session, turn);
  }
}
