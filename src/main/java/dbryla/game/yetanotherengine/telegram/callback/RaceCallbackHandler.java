package dbryla.game.yetanotherengine.telegram.callback;

import dbryla.game.yetanotherengine.domain.subject.CharacterClass;
import dbryla.game.yetanotherengine.domain.subject.Race;
import dbryla.game.yetanotherengine.session.Session;
import dbryla.game.yetanotherengine.telegram.BuildingFactory;
import dbryla.game.yetanotherengine.telegram.Commons;
import dbryla.game.yetanotherengine.telegram.SessionFactory;
import dbryla.game.yetanotherengine.telegram.TelegramClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@AllArgsConstructor
public class RaceCallbackHandler implements CallbackHandler {

  private final SessionFactory sessionFactory;
  private final Commons commons;
  private final TelegramClient telegramClient;
  private final BuildingFactory buildingFactory;

  @Override
  public void execute(Update update) {
    Long chatId = update.getCallbackQuery().getMessage().getChatId();
    Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
    String sessionId = commons.getSessionId(update.getCallbackQuery().getMessage(), update.getCallbackQuery().getFrom());
    Session session = sessionFactory.getSession(sessionId);
    CharacterClass characterClass = session.getCharacterClass();
    String callbackData = update.getCallbackQuery().getData();
    try {
      Race race = Race.valueOf(callbackData);
      session.setRace(race);
      buildingFactory.raceSpecialCommunicate(race).ifPresent(session::addLastCommunicate);
      session.addLastCommunicate(buildingFactory.chooseWeaponCommunicate(characterClass, race));
      session.addLastCommunicate(buildingFactory.chooseWeaponCommunicate(characterClass, race));
      buildingFactory.chooseArmorCommunicate(characterClass, race).ifPresent(session::addLastCommunicate);
    } catch (IllegalArgumentException e) {
      session.addNextCommunicate(buildingFactory.chooseRaceCommunicate(callbackData));
    }
    Integer originalMessageId = commons.getOriginalMessageId(update);
    telegramClient.deleteMessage(chatId, messageId);
    telegramClient.sendReplyKeyboard(session.getNextCommunicate(), chatId, originalMessageId);
  }
}
