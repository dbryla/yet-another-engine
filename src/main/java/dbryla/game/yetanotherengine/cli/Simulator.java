package dbryla.game.yetanotherengine.cli;

import dbryla.game.yetanotherengine.domain.Abilities;
import dbryla.game.yetanotherengine.domain.Action;
import dbryla.game.yetanotherengine.domain.Instrument;
import dbryla.game.yetanotherengine.domain.ai.ArtificialIntelligence;
import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.operations.Operation;
import dbryla.game.yetanotherengine.domain.state.StateMachine;
import dbryla.game.yetanotherengine.domain.state.StateMachineFactory;
import dbryla.game.yetanotherengine.domain.state.storage.StateStorage;
import dbryla.game.yetanotherengine.domain.subjects.Monster;
import dbryla.game.yetanotherengine.domain.subjects.classes.Fighter;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Weapon;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Profile("cli")
public class Simulator {

  private final StateStorage stateStorage;
  private final ArtificialIntelligence artificialIntelligence;
  private final StateMachineFactory stateMachineFactory;
  private final ConsolePresenter presenter;
  private final Operation attackOperation;
  private final EventHub eventHub;

  public void start() {
    final String player1 = "Clemens";
    final String player2 = "Maria";
    final String blueTeam = "blue";
    Abilities defaultAbilities = new Abilities(10, 10, 10, 10, 10, 10);
    stateStorage.save(Fighter.builder()
        .name(player1)
        .affiliation(blueTeam)
        .weapon(Weapon.CLUB)
        .abilities(defaultAbilities)
        .build());
    stateStorage.save(Fighter.builder()
        .name(player2)
        .affiliation(blueTeam)
        .weapon(Weapon.LONGBOW)
        .abilities(defaultAbilities)
        .build());
    final String enemy = "Borg";
    Monster enemyFighter = Monster.builder()
        .name(enemy)
        .healthPoints(25)
        .abilities(defaultAbilities)
        .weapon(Weapon.GREATSWORD)
        .build();
    stateStorage.save(enemyFighter);
    artificialIntelligence.initSubject(enemyFighter);
    long gameId = 123L;
    StateMachine stateMachine = stateMachineFactory.createInMemoryStateMachine();
    presenter.showStatus();
    while (!stateMachine.isInTerminalState()) {
      stateMachine.getNextSubject().ifPresent(subject -> {
            switch (subject.getName()) {
              case player1:
                stateMachine.execute(new Action(player1, enemy, attackOperation, new Instrument(Weapon.CLUB)))
                    .forEach(event -> eventHub.send(event, gameId));
                break;
              case player2:
                stateMachine.execute(new Action(player2, enemy, attackOperation, new Instrument(Weapon.LONGBOW)))
                    .forEach(event -> eventHub.send(event, gameId));
              case enemy:
                stateMachine.execute(artificialIntelligence.action(enemy));
            }
          }
      );
    }
    presenter.showStatus();
  }

}
