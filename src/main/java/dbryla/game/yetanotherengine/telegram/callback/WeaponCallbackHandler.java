package dbryla.game.yetanotherengine.telegram.callback;

import dbryla.game.yetanotherengine.domain.game.Action;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.game.SubjectTurn;
import dbryla.game.yetanotherengine.domain.operations.ActionData;
import dbryla.game.yetanotherengine.domain.operations.OperationType;
import dbryla.game.yetanotherengine.session.Session;
import dbryla.game.yetanotherengine.telegram.Commons;
import dbryla.game.yetanotherengine.telegram.FightFactory;
import dbryla.game.yetanotherengine.telegram.SessionFactory;
import dbryla.game.yetanotherengine.telegram.TelegramClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@AllArgsConstructor
public class WeaponCallbackHandler implements CallbackHandler {

  private final Commons commons;
  private final SessionFactory sessionFactory;
  private final TelegramClient telegramClient;
  private final FightFactory fightFactory;

  @Override
  public void execute(Update update) {
    Long chatId = update.getCallbackQuery().getMessage().getChatId();
    Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
    String sessionId = commons.getSessionId(update.getCallbackQuery().getMessage(), update.getCallbackQuery().getFrom());
    Session session = sessionFactory.getSession(sessionId);
    Game game = sessionFactory.getGame(chatId);
    String playerName = commons.getCharacterName(update.getCallbackQuery().getFrom());
    telegramClient.deleteMessage(chatId, messageId);
    fightFactory.targetCommunicate(game, playerName, session.getWeapon())
        .ifPresentOrElse(
            communicate -> telegramClient.sendReplyKeyboard(communicate, chatId, commons.getOriginalMessageId(update)),
            () -> commons.executeTurn(game, session,
                SubjectTurn.of(new Action(playerName, game.getPossibleTargets(playerName, session.getWeapon()).get(0),
                    OperationType.ATTACK, new ActionData(session.getWeapon())))));
  }
}
