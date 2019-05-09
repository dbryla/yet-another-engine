package dbryla.game.yetanotherengine.telegram.callback;

import dbryla.game.yetanotherengine.domain.subject.CharacterClass;
import dbryla.game.yetanotherengine.domain.subject.Race;
import dbryla.game.yetanotherengine.session.BuildSession;
import dbryla.game.yetanotherengine.telegram.BuildingFactory;
import dbryla.game.yetanotherengine.telegram.SessionFactory;
import dbryla.game.yetanotherengine.telegram.TelegramClient;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Profile("tg")
public class RaceCallbackHandler implements CallbackHandler {

  private final SessionFactory sessionFactory;
  private final TelegramClient telegramClient;
  private final BuildingFactory buildingFactory;

  @Override
  public void execute(Callback callback) {
    BuildSession session = sessionFactory.getBuildSession(callback.getSessionId());
    CharacterClass characterClass = session.getCharacterClass();
    try {
      Race race = Race.valueOf(callback.getData());
      session.setRace(race);
      buildingFactory.raceSpecialCommunicate(race).ifPresent(session::addLastCommunicate);
      session.addLastCommunicate(buildingFactory.chooseWeaponCommunicate(characterClass, race));
      session.addLastCommunicate(buildingFactory.chooseWeaponCommunicate(characterClass, race));
      buildingFactory.chooseArmorCommunicate(characterClass, race).ifPresent(session::addLastCommunicate);
    } catch (IllegalArgumentException e) {
      session.addNextCommunicate(buildingFactory.chooseRaceCommunicate(callback.getData()));
    }
    telegramClient.deleteMessage(callback.getChatId(), callback.getMessageId());
    telegramClient.sendReplyKeyboard(session.getNextCommunicate(), callback.getChatId(), callback.getOriginalMessageId());
  }
}
