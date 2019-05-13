package dbryla.game.yetanotherengine.telegram;

import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.game.GameFactory;
import dbryla.game.yetanotherengine.domain.subject.AbilityScoresSupplier;
import dbryla.game.yetanotherengine.domain.subject.SubjectProperties;
import dbryla.game.yetanotherengine.session.BuildSession;
import dbryla.game.yetanotherengine.session.FightSession;
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

  public BuildSession createBuildSession(String sessionId, String playerName) {
    List<Integer> abilityScores = abilityScoresSupplier.get();
    BuildSession session = new BuildSession(playerName,
        new LinkedList<>(List.of(
            buildingFactory.chooseClassCommunicate(),
            buildingFactory.chooseRaceGroupCommunicate(),
            buildingFactory.assignAbilitiesCommunicate(abilityScores))),
        abilityScores);
    sessionStorage.put(sessionId, session);
    return session;
  }

  public FightSession createFightSession(String sessionId, String playerName, SubjectProperties subjectProperties) {
    FightSession session = new FightSession(playerName, subjectProperties);
    sessionStorage.put(sessionId, session);
    return session;
  }

  public FightSession getFightSession(String sessionId) {
    return sessionStorage.getFightSession(sessionId);
  }

  public BuildSession getBuildSession(String sessionId) {
    return sessionStorage.getBuildSession(sessionId);
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
