package dbryla.game.yetanotherengine.domain;

import dbryla.game.yetanotherengine.InputProvider;
import dbryla.game.yetanotherengine.domain.ai.ArtificialIntelligence;
import dbryla.game.yetanotherengine.domain.state.StateMachineFactory;
import dbryla.game.yetanotherengine.domain.state.storage.StateStorage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GameFactory {

  private final StateStorage stateStorage;
  private final ArtificialIntelligence artificialIntelligence;
  private final StateMachineFactory stateMachineFactory;
  private final InputProvider inputProvider;
  private final GameOptions gameOptions;

  public Game newGame() {
    return new Game(stateStorage, stateMachineFactory, artificialIntelligence, inputProvider, gameOptions);
  }

}
