package dbryla.game.yetanotherengine.cli;

import dbryla.game.yetanotherengine.Presenter;
import dbryla.game.yetanotherengine.domain.Action;
import dbryla.game.yetanotherengine.domain.ai.ArtificialIntelligence;
import dbryla.game.yetanotherengine.domain.operations.Operation;
import dbryla.game.yetanotherengine.domain.state.StateMachine;
import dbryla.game.yetanotherengine.domain.state.StateMachineFactory;
import dbryla.game.yetanotherengine.domain.state.storage.StateStorage;
import dbryla.game.yetanotherengine.domain.subjects.classes.Fighter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class Simulator {

  private final StateStorage stateStorage;
  private final ArtificialIntelligence artificialIntelligence;
  private final StateMachineFactory stateMachineFactory;
  private final Presenter presenter;
  private final Operation attackOperation;

  public void start() {
    final String player1 = "Clemens";
    final String player2 = "Maria";
    final String blueTeam = "blue";
    stateStorage.save(new Fighter(player1, blueTeam));
    stateStorage.save(new Fighter(player2, blueTeam));
    final String greenTeam = "green";
    final String enemy = "Borg";
    Fighter enemyFighter = Fighter.builder()
        .name(enemy)
        .affiliation(greenTeam)
        .healthPoints(30)
        .build();
    stateStorage.save(enemyFighter);
    artificialIntelligence.initSubject(enemyFighter);
    StateMachine stateMachine = stateMachineFactory.createInMemoryStateMachine();
    while (!stateMachine.isInTerminalState()) {
      presenter.showStatus();
      stateMachine.getNextSubject().ifPresent(subject -> {
            switch (subject.getName()) {
              case player1:
                stateMachine.execute(new Action(player1, enemy, attackOperation));
                break;
              case player2:
                stateMachine.execute(new Action(player2, enemy, attackOperation));
                break;
              case enemy:
                stateMachine.execute(artificialIntelligence.attackAction(enemy));
            }
          }
      );
    }
    presenter.showStatus();
  }

}
