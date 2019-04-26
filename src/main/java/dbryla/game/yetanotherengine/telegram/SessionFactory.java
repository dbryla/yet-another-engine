package dbryla.game.yetanotherengine.telegram;

import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.game.GameFactory;
import dbryla.game.yetanotherengine.domain.subject.AbilityScoresSupplier;
import dbryla.game.yetanotherengine.domain.subject.CharacterClass;
import dbryla.game.yetanotherengine.domain.subject.Race;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.session.Session;
import dbryla.game.yetanotherengine.session.SessionStorage;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

import static dbryla.game.yetanotherengine.telegram.BuildingFactory.*;

@Component
@Profile("tg")
@AllArgsConstructor
public class SessionFactory {

  private final BuildingFactory buildingFactory;
  private final AbilityScoresSupplier abilityScoresSupplier;
  private final SessionStorage sessionStorage;
  private final GameFactory gameFactory;

  public Session createCharacterCreationCommunicates(String sessionId, String playerName) {
    List<Integer> abilityScores = abilityScoresSupplier.get();
    Session session = new Session(playerName,
        new LinkedList<>(List.of(
            buildingFactory.chooseClassCommunicate(),
            buildingFactory.chooseRaceCommunicate(),
            buildingFactory.assignAbilitiesCommunicate(abilityScores))),
        abilityScores);
    sessionStorage.put(sessionId, session);
    return session;
  }

  void updateSession(String messageText, Session session, String callbackData) {
    session.update(messageText, callbackData);
    if (messageText.contains(RACE)) {
      CharacterClass characterClass = CharacterClass.valueOf((String) session.getData().get(CLASS));
      Race race = Race.valueOf(callbackData);
      session.addLastCommunicate(buildingFactory.chooseWeaponCommunicate(characterClass, race));
      session.addLastCommunicate(buildingFactory.chooseWeaponCommunicate(characterClass, race));
      buildingFactory.chooseArmorCommunicate(characterClass, race).ifPresent(session::addLastCommunicate);
    }
    if (messageText.contains(ABILITIES)) {
      buildingFactory.nextAbilityAssignment(session, callbackData).ifPresent(session::addNextCommunicate);
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
    Game game = sessionStorage.get(chatId);
    if (game == null) {
      sessionStorage.put(chatId, gameFactory.newGame(chatId));
      game = sessionStorage.get(chatId);
    }
    return game;
  }

  public Game getGame(Long chatId) {
    return sessionStorage.get(chatId);
  }

}
