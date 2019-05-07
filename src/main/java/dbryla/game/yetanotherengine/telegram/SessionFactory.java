package dbryla.game.yetanotherengine.telegram;

import static dbryla.game.yetanotherengine.telegram.BuildingFactory.ABILITIES;
import static dbryla.game.yetanotherengine.telegram.BuildingFactory.CLASS;
import static dbryla.game.yetanotherengine.telegram.BuildingFactory.EXTRA_ABILITIES;
import static dbryla.game.yetanotherengine.telegram.BuildingFactory.RACE;

import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.game.GameFactory;
import dbryla.game.yetanotherengine.domain.subject.AbilityScoresSupplier;
import dbryla.game.yetanotherengine.domain.subject.CharacterClass;
import dbryla.game.yetanotherengine.domain.subject.Race;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.session.Session;
import dbryla.game.yetanotherengine.session.SessionStorage;
import dbryla.game.yetanotherengine.telegram.session.GameStorage;
import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("tg")
@AllArgsConstructor
public class SessionFactory {

  private final BuildingFactory buildingFactory;
  private final AbilityScoresSupplier abilityScoresSupplier;
  private final SessionStorage sessionStorage;
  private final GameStorage gameStorage;
  private final GameFactory gameFactory;

  public Session createCharacterCreationCommunicates(String sessionId, String playerName) {
    List<Integer> abilityScores = abilityScoresSupplier.get();
    Session session = new Session(playerName,
        new LinkedList<>(List.of(
            buildingFactory.chooseClassCommunicate(),
            buildingFactory.chooseRaceGroupCommunicate(),
            buildingFactory.assignAbilitiesCommunicate(abilityScores))),
        abilityScores);
    sessionStorage.put(sessionId, session);
    return session;
  }

  void updateSession(String messageText, Session session, String callbackData) {
    if (session != null) {
      session.update(messageText, callbackData);
      if (messageText.contains(RACE)) {
        CharacterClass characterClass = CharacterClass.valueOf((String) session.getGenericData().get(CLASS));
        try {
          Race race = Race.valueOf(callbackData);
          buildingFactory.raceSpecialCommunicate(race).ifPresent(session::addLastCommunicate);
          session.addLastCommunicate(buildingFactory.chooseWeaponCommunicate(characterClass, race));
          session.addLastCommunicate(buildingFactory.chooseWeaponCommunicate(characterClass, race));
          buildingFactory.chooseArmorCommunicate(characterClass, race).ifPresent(session::addLastCommunicate);
        } catch (IllegalArgumentException e) {
          session.addNextCommunicate(buildingFactory.chooseRaceCommunicate(callbackData));
        }
      }
      if (messageText.contains(ABILITIES)) {
        buildingFactory.nextAbilityAssignment(session, callbackData).ifPresent(session::addNextCommunicate);
      }
      if (messageText.contains(EXTRA_ABILITIES)) {
        buildingFactory.extraAbilitiesCommunicate(session, callbackData).ifPresent(session::addNextCommunicate);
      }
    }
  }

  public Session createSession(String sessionId, String playerName, Subject subject) {
    Session session = new Session(playerName, subject);
    sessionStorage.put(sessionId, session);
    return session;
  }

  public Session getSession(String sessionId) {
    return sessionStorage.get(sessionId);
  }

  public Game getGameOrCreate(Long chatId) {
    Game game = gameStorage.get(chatId);
    if (game == null) {
      gameStorage.put(chatId, gameFactory.newGame(chatId));
      game = gameStorage.get(chatId);
    }
    return game;
  }

  public Game getGame(Long chatId) {
    return gameStorage.get(chatId);
  }

}
