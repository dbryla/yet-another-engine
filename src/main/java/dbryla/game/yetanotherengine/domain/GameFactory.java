package dbryla.game.yetanotherengine.domain;

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
  private final GameOptions gameOptions;

  public Game newGame(Long gameId) {
    return new Game(gameId, stateStorage, stateMachineFactory, artificialIntelligence, gameOptions);
  }

}
