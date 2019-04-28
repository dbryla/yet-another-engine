package dbryla.game.yetanotherengine.telegram;

import dbryla.game.yetanotherengine.db.CharacterRepository;
import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.game.Action;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.game.SubjectTurn;
import dbryla.game.yetanotherengine.domain.operations.ActionData;
import dbryla.game.yetanotherengine.domain.operations.OperationType;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.SubjectFactory;
import dbryla.game.yetanotherengine.session.Session;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;

import static dbryla.game.yetanotherengine.telegram.BuildingFactory.ABILITIES;
import static dbryla.game.yetanotherengine.telegram.FightFactory.*;

@Component
@Slf4j
@AllArgsConstructor
@Profile("tg")
public class CallbackHandler {

  private final SessionFactory sessionFactory;
  private final TelegramClient telegramClient;
  private final FightFactory fightFactory;
  private final SubjectFactory subjectFactory;
  private final CharacterRepository characterRepository;
  private final Commons commons;

  public void execute(Update update) {
    String messageText = update.getCallbackQuery().getMessage().getText();
    Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
    String sessionId = commons.getSessionId(update.getCallbackQuery().getMessage(), update.getCallbackQuery().getFrom());
    log.trace("Callback: {} no:{} [{}]", messageText, messageId, sessionId);
    String playerName = commons.getCharacterName(update.getCallbackQuery().getFrom());
    if (!playerName.equals(commons.getCharacterName(update.getCallbackQuery().getMessage().getReplyToMessage().getFrom()))) {
      log.trace("Aborting handling callback. Not the owner of original command.");
      return;
    }
    String callbackData = update.getCallbackQuery().getData();
    Long chatId = update.getCallbackQuery().getMessage().getChatId();
    Session session = sessionFactory.getSession(sessionId);
    sessionFactory.updateSession(messageText, session, callbackData);
    session.getNextCommunicate()
        .ifPresentOrElse(
            communicate -> handleCharacterBuilding(communicate, messageText, messageId, chatId, getOriginalMessageId(update)),
            () -> handleFightOrCharacterCreation(session, playerName, callbackData,
                messageText, messageId, getOriginalMessageId(update), chatId));
  }

  private Integer getOriginalMessageId(Update update) {
    return update.getCallbackQuery().getMessage().getReplyToMessage().getMessageId();
  }

  private void handleCharacterBuilding(Communicate communicate, String messageText, Integer messageId,
                                       Long chatId, Integer originalMessageId) {
    if (messageText.contains(ABILITIES) && ABILITIES.equals(communicate.getText())) {
      telegramClient.sendEditKeyboard(communicate, chatId, messageId);
    } else {
      telegramClient.deleteMessage(chatId, messageId);
      telegramClient.sendReplyKeyboard(communicate, chatId, originalMessageId);
    }
  }

  private void handleFightOrCharacterCreation(Session session, String playerName, String callbackData, String messageText,
                                              Integer messageId, Integer originalMessageId, Long chatId) {
    Game game = sessionFactory.getGame(chatId);
    if (messageText.startsWith(SPELL)) {
      handleSpellCallback(playerName, callbackData, messageId, originalMessageId, chatId, game, session);
    } else if (messageText.startsWith(TARGETS)) {
      handleTargetsCallback(session, playerName, callbackData, messageId, chatId, game);
    } else if (messageText.startsWith(WEAPON)) {
      handleWeaponCallback(session, playerName, messageId, originalMessageId, chatId, game);
    } else if (messageText.startsWith(MOVE)) {
      handleMoveCallback(session, playerName, callbackData, messageId, chatId, game);
    } else {
      handleCharacterCreation(chatId, session, messageId);
    }
  }

  private void handleMoveCallback(Session session, String playerName, String callbackData, Integer messageId, Long chatId, Game game) {
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

  private void handleWeaponCallback(Session session, String playerName, Integer messageId, Integer originalMessageId, Long chatId, Game game) {
    telegramClient.deleteMessage(chatId, messageId);
    fightFactory.targetCommunicate(game, playerName, session.getWeapon())
        .ifPresentOrElse(
            communicate -> telegramClient.sendReplyKeyboard(communicate, chatId, originalMessageId),
            () -> commons.executeTurn(game, session,
                SubjectTurn.of(new Action(playerName, game.getPossibleTargets(playerName, session.getWeapon()).get(0),
                    OperationType.ATTACK, new ActionData(session.getWeapon())))));
  }

  private void handleTargetsCallback(Session session, String playerName, String callbackData, Integer messageId, Long chatId, Game game) {
    if (session.isSpellCasting()) {
      handleSpellOnTarget(session, playerName, messageId, chatId, game);
    } else {
      SubjectTurn turn = SubjectTurn.of(new Action(playerName, callbackData, OperationType.ATTACK, new ActionData(session.getWeapon())));
      commons.executeTurnAndDeleteMessage(game, session, turn, chatId, messageId);
    }
  }

  private void handleSpellOnTarget(Session session, String playerName, Integer messageId, Long chatId, Game game) {
    Spell spell = Spell.valueOf((String) session.getData().get(SPELL));
    if (session.areAllTargetsAcquired()) {
      SubjectTurn turn = SubjectTurn.of(new Action(playerName, session.getTargets(), OperationType.SPELL_CAST, new ActionData(spell)));
      commons.executeTurnAndDeleteMessage(game, session, turn, chatId, messageId);
    } else {
      Optional<Communicate> communicate = fightFactory.targetCommunicate(game, playerName, spell, session.getTargets());
      communicate.ifPresent(value -> telegramClient.sendEditKeyboard(value, chatId, messageId));
    }
  }

  private void handleSpellCallback(String playerName, String chosenSpell, Integer messageId, Integer originalMessageId,
                                   Long chatId, Game game, Session session) {
    Spell spell = Spell.valueOf(chosenSpell);
    telegramClient.deleteMessage(chatId, messageId);
    List<String> possibleTargets = game.getPossibleTargets(playerName, spell);
    if (!spell.isAreaOfEffectSpell() && possibleTargets.size() > spell.getMaximumNumberOfTargets()) {
      Optional<Communicate> communicate = fightFactory.targetCommunicate(possibleTargets);
      if (communicate.isPresent()) {
        telegramClient.sendReplyKeyboard(communicate.get(), chatId, originalMessageId);
        return;
      }
    }
    SubjectTurn turn = SubjectTurn.of(new Action(playerName, possibleTargets, OperationType.SPELL_CAST, new ActionData(spell)));
    commons.executeTurn(game, session, turn);
  }

  private void handleCharacterCreation(Long chatId, Session session, Integer messageId) {
    telegramClient.deleteMessage(chatId, messageId);
    if (session.getSubject() == null) {
      Subject subject = subjectFactory.fromSession(session);
      session.setSubject(subject);
      Game game = sessionFactory.getGameOrCreate(chatId);
      game.createPlayerCharacter(subject);
      telegramClient.sendTextMessage(chatId, session.getPlayerName() + ": Your character has been created.\n" + subject);
      characterRepository.findByName(session.getPlayerName()).ifPresent(characterRepository::delete);
      characterRepository.save(subjectFactory.toCharacter(subject));
    }
  }

}
