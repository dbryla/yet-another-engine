package dbryla.game.yetanotherengine.domain.game;

import dbryla.game.yetanotherengine.domain.ai.ArtificialIntelligence;
import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.game.state.StateMachineFactory;
import dbryla.game.yetanotherengine.domain.game.state.storage.SubjectStorage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GameFactory {

  private final SubjectStorage subjectStorage;
  private final ArtificialIntelligence artificialIntelligence;
  private final StateMachineFactory stateMachineFactory;
  private final EventHub eventHub;

  public Game newGame(Long gameId) {
    return new Game(gameId, subjectStorage, stateMachineFactory, artificialIntelligence, eventHub);
  }

}
