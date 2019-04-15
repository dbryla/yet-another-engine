package dbryla.game.yetanotherengine.telegram;

import static dbryla.game.yetanotherengine.telegram.BuildingFactory.ABILITIES;
import static dbryla.game.yetanotherengine.telegram.FightFactory.SPELL;
import static dbryla.game.yetanotherengine.telegram.FightFactory.TARGET;
import static dbryla.game.yetanotherengine.telegram.TelegramHelpers.getCharacterName;
import static dbryla.game.yetanotherengine.telegram.TelegramHelpers.getSessionId;

import dbryla.game.yetanotherengine.db.CharacterRepository;
import dbryla.game.yetanotherengine.domain.Action;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.operations.Instrument;
import dbryla.game.yetanotherengine.domain.operations.OperationType;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.SubjectFactory;
import dbryla.game.yetanotherengine.session.Session;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

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
            () -> handleFightOrFinishCharacterCreation(session, playerName, callbackData,
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

  private void handleFightOrFinishCharacterCreation(Session session, String playerName, String callbackData,
                                                    String messageText, Integer messageId, Integer originalMessageId, Long chatId) {
    Game game = sessionFactory.getGame(chatId);
    if (messageText.startsWith(SPELL)) {
      handleSpellCallback(playerName, callbackData, messageId, originalMessageId, chatId, game, session);
    } else if (messageText.startsWith(TARGET)) {
      if (session.isSpellCasting()) {
        Spell spell = Spell.valueOf((String) session.getData().get(SPELL));
        if (session.areAllTargetsAcquired()) {
          spellCastOnManyTargets(session, playerName, messageId, chatId, game, spell);
        } else {
          Optional<Communicate> communicate = fightFactory.targetCommunicate(game, spell.isPositiveSpell(), session.getTargets());
          communicate.ifPresent(value -> telegramClient.sendEditKeyboard(value, chatId, messageId));
        }
      } else {
        telegramClient.deleteMessage(chatId, messageId);
        game.executeAction(
            new Action(playerName, callbackData, OperationType.ATTACK, new Instrument(session.getSubject().getEquipment().getWeapon())));
        session.clearTargets();
      }
    } else {
      telegramClient.deleteMessage(chatId, messageId);
      createCharacterIfNeeded(chatId, session);
    }
  }

  private void handleSpellCallback(String playerName, String chosenSpell, Integer messageId, Integer originalMessageId,
                                   Long chatId, Game game, Session session) {
    Spell spell = Spell.valueOf(chosenSpell);
    telegramClient.deleteMessage(chatId, messageId);
    boolean isPositiveSpell = spell.isPositiveSpell();
    if (!spell.hasUnlimitedTargets() && game.getAllAliveSubjectNames(isPositiveSpell).size() > spell.getMaximumNumberOfTargets()) {
      Optional<Communicate> communicate = fightFactory.targetCommunicate(game, isPositiveSpell);
      if (communicate.isPresent()) {
        telegramClient.sendReplyKeyboard(communicate.get(), chatId, originalMessageId);
        return;
      }
    }
    game.executeAction(
        new Action(playerName, game.getAllAliveSubjectNames(spell.isPositiveSpell()), OperationType.SPELL_CAST, new Instrument(spell)));
    session.clearTargets();
  }

  private void spellCastOnManyTargets(Session session, String playerName, Integer messageId, Long chatId, Game game, Spell spell) {
    telegramClient.deleteMessage(chatId, messageId);
    game.executeAction(new Action(playerName, session.getTargets(), OperationType.SPELL_CAST, new Instrument(spell)));
    session.clearTargets();
  }

  private void createCharacterIfNeeded(Long chatId, Session session) {
    if (session.getSubject() == null) {
      Subject subject = subjectFactory.fromSession(session);
      session.setSubject(subject);
      Game game = sessionFactory.getGameOrCreate(chatId);
      game.createCharacter(subject);
      telegramClient.sendTextMessage(chatId, session.getPlayerName() + ": Your character has been created.\n" + subject);
      characterRepository.findByName(session.getPlayerName()).ifPresent(characterRepository::delete);
      characterRepository.save(subjectFactory.toCharacter(subject));
    }
  }

}
