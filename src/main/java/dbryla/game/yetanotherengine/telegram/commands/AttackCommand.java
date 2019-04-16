package dbryla.game.yetanotherengine.telegram.commands;

import dbryla.game.yetanotherengine.domain.game.Action;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.game.SubjectTurn;
import dbryla.game.yetanotherengine.domain.operations.ActionData;
import dbryla.game.yetanotherengine.domain.operations.OperationType;
import dbryla.game.yetanotherengine.session.Session;
import dbryla.game.yetanotherengine.telegram.FightFactory;
import dbryla.game.yetanotherengine.telegram.SessionFactory;
import dbryla.game.yetanotherengine.telegram.TelegramClient;
import dbryla.game.yetanotherengine.telegram.TelegramHelpers;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static dbryla.game.yetanotherengine.telegram.TelegramHelpers.getSessionId;

@Component
@AllArgsConstructor
@Profile("tg")
public class AttackCommand {
  private final SessionFactory sessionFactory;
  private final TelegramClient telegramClient;
  private final FightFactory fightFactory;

  public void execute(Update update) {
    Long chatId = update.getMessage().getChatId();
    Game game = sessionFactory.getGame(chatId);
    Session session = sessionFactory.getSession(getSessionId(update.getMessage(), update.getMessage().getFrom()));
    String playerName = session.getPlayerName();
    if (game.isStarted() && !game.isEnded() && TelegramHelpers.isNextUser(playerName, game)) {
      session.setSpellCasting(false);
      fightFactory.targetCommunicate(game)
          .ifPresentOrElse(
              communicate -> telegramClient.sendReplyKeyboard(communicate, chatId, update.getMessage().getMessageId()),
              () -> game.execute(
                  SubjectTurn.of(new Action(playerName, game.getAllAliveEnemyNames().get(0),
                      OperationType.ATTACK, new ActionData(session.getSubject().getEquipment().getWeapon())))));
    }
  }
}
