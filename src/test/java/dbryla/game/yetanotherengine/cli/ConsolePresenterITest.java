package dbryla.game.yetanotherengine.cli;

import dbryla.game.yetanotherengine.domain.encounters.MonstersFactory;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.game.GameFactory;
import dbryla.game.yetanotherengine.domain.game.state.storage.StateStorage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("cli")
class ConsolePresenterITest {

  @Autowired
  private ConsolePresenter consolePresenter;

  @Autowired
  private GameFactory gameFactory;

  @Autowired
  private StateStorage stateStorage;

  @Autowired
  private MonstersFactory monstersFactory;

  @Test
  void shouldReturnOnlyAliveTargets() {
    Long gameId = 123L;
    Game game = gameFactory.newGame(gameId);
    game.createNonPlayableCharacters(monstersFactory.createEncounter(1));
    stateStorage.findAll(gameId).stream()
        .findAny()
        .ifPresent(subject -> stateStorage.save(gameId, subject.of(0)));

    List<String> availableTargets = consolePresenter.showAvailableEnemyTargets(game);

    assertThat(availableTargets).allMatch(subjectName -> !stateStorage.findByIdAndName(gameId, subjectName).get().isTerminated());
  }
}