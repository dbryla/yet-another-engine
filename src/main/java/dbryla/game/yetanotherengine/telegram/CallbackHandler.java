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
import static dbryla.game.yetanotherengine.telegram.TelegramHelpers.*;

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

  public void execute(Update update) {
    String messageText = update.getCallbackQuery().getMessage().getText();
    Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
    String sessionId = getSessionId(update.getCallbackQuery().getMessage(), update.getCallbackQuery().getFrom());
    log.trace("Callback: {} no:{} [{}]", messageText, messageId, sessionId);
    String playerName = getCharacterName(update.getCallbackQuery().getFrom());
    if (!playerName.equals(getCharacterName(update.getCallbackQuery().getMessage().getReplyToMessage().getFrom()))) {
      log.trace("Aborting handling callback. Not the owner of original command.");
      return;
    }
    String callbackData = update.getCallbackQuery().getData();
    Long chatId = update.getCallbackQuery().getMessage().getChatId();
    Session session = sessionFactory.getSession(sessionId);
    sessionFactory.updateSession(messageText, session, callbackData);
    session.getNextBuildingCommunicate()
        .ifPresentOrElse(
            communicate -> handleCharacterBuilding(communicate, messageText, messageId, chatId, getOriginalMessageId(update)),
            () -> handleFightAndCharacterCreation(session, playerName, callbackData,
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

  private void handleFightAndCharacterCreation(Session session, String playerName, String callbackData, String messageText,
      Integer messageId, Integer originalMessageId, Long chatId) {
    Game game = sessionFactory.getGame(chatId);
    if (messageText.startsWith(SPELL)) {
      handleSpellCallback(playerName, callbackData, messageId, originalMessageId, chatId, game, session);
    } else if (messageText.startsWith(TARGET)) {
      if (session.isSpellCasting()) {
        handleSpellTarget(session, playerName, messageId, chatId, game);
      } else {
        handleWeaponAttack(session, playerName, callbackData, messageId, chatId, game);
      }
    } else if (messageText.startsWith(WEAPON)) {
      telegramClient.deleteMessage(chatId, messageId);
      fightFactory.targetCommunicate(game, playerName, session.getWeapon())
          .ifPresentOrElse(
              communicate -> telegramClient.sendReplyKeyboard(communicate, chatId, originalMessageId),
              () -> executeTurn(game, session,
                  SubjectTurn.of(new Action(playerName, game.getAllAliveEnemyNames().get(0),
                      OperationType.ATTACK, new ActionData(session.getWeapon())))));
      session.cleanUpCallbackData();
    } else {
      if (messageText.startsWith(MOVE)) {
        if (!session.isMoving()) {
          handleMoveCallback(session, callbackData, chatId, game);
          if (isNextUser(playerName, game)) {
            session.setMoving(true);
            telegramClient.sendTextMessage(chatId,
                playerName + " what do you want to do next: /move /pass /attack" + getSpellCommandIfApplicable(session.getSubject()));
          }
        } else {
          SubjectTurn turn = SubjectTurn.of(
              new Action(playerName, List.of(), OperationType.MOVE, new ActionData(Position.valueOf(Integer.valueOf(callbackData)))));
          executeTurn(game, session, turn);
        }
      }
      telegramClient.deleteMessage(chatId, messageId);
      createCharacterIfNeeded(chatId, session);
    }
  }

  private void handleMoveCallback(Session session, String callbackData, Long chatId, Game game) {
    Position newPosition = Position.valueOf(Integer.valueOf(callbackData));
    game.moveSubject(session.getPlayerName(), newPosition);
    telegramClient.sendTextMessage(chatId, session.getPlayerName() + " moves to " + newPosition + ".");
  }

  private void handleWeaponAttack(Session session, String playerName, String callbackData, Integer messageId, Long chatId, Game game) {
    telegramClient.deleteMessage(chatId, messageId);
    SubjectTurn turn = SubjectTurn.of(
        new Action(playerName, callbackData, OperationType.ATTACK, new ActionData(session.getSubject().getEquipment().getWeapons().get(0))));
    executeTurn(game, session, turn);
  }

  private void handleSpellTarget(Session session, String playerName, Integer messageId, Long chatId, Game game) {
    Spell spell = Spell.valueOf((String) session.getData().get(SPELL));
    if (session.areAllTargetsAcquired()) {
      spellCastOnManyTargets(session, playerName, messageId, chatId, game, spell);
    } else {
      Optional<Communicate> communicate = fightFactory.targetCommunicate(game, playerName, spell, session.getTargets());
      communicate.ifPresent(value -> telegramClient.sendEditKeyboard(value, chatId, messageId));
    }
  }

  private void handleSpellCallback(String playerName, String chosenSpell, Integer messageId, Integer originalMessageId,
      Long chatId, Game game, Session session) {
    Spell spell = Spell.valueOf(chosenSpell);
    telegramClient.deleteMessage(chatId, messageId);
    if (!spell.isAreaOfEffectSpell() && game.getPossibleTargets(game.getSubject(playerName), spell).size() > spell.getMaximumNumberOfTargets()) {
      Optional<Communicate> communicate = fightFactory.targetCommunicate(game, playerName, spell);
      if (communicate.isPresent()) {
        telegramClient.sendReplyKeyboard(communicate.get(), chatId, originalMessageId);
        return;
      }
    }
    SubjectTurn turn = SubjectTurn.of(
        new Action(playerName, game.getPossibleTargets(game.getSubject(playerName), spell), OperationType.SPELL_CAST, new ActionData(spell)));
    executeTurn(game, session, turn);
  }

  private void spellCastOnManyTargets(Session session, String playerName, Integer messageId, Long chatId, Game game, Spell spell) {
    telegramClient.deleteMessage(chatId, messageId);
    SubjectTurn turn = SubjectTurn.of(new Action(playerName, session.getTargets(), OperationType.SPELL_CAST, new ActionData(spell)));
    executeTurn(game, session, turn);
  }

  private void createCharacterIfNeeded(Long chatId, Session session) {
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
