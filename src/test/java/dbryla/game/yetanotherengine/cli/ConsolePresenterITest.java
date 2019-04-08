package dbryla.game.yetanotherengine.cli;

import dbryla.game.yetanotherengine.domain.Game;
import dbryla.game.yetanotherengine.domain.GameFactory;
import dbryla.game.yetanotherengine.domain.state.storage.StateStorage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ConsolePresenterITest {

  @Autowired
  private ConsolePresenter consolePresenter;

  @Autowired
  private GameFactory gameFactory;

  @Autowired
  private StateStorage stateStorage;

  @Test
  void shouldReturnOnlyAliveTargets() {
    Game game = gameFactory.newGame();
    game.createEnemies(playersNumber);
    StreamSupport.stream(stateStorage.findAll().spliterator(), false)
        .findAny()
        .ifPresent(subject -> stateStorage.save(subject.of(0)));

    List<String> availableTargets = consolePresenter.showAvailableEnemyTargets(game);

    assertThat(availableTargets).allMatch(subjectName -> !stateStorage.findByName(subjectName).get().isTerminated());

  }
}