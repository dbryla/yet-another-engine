package dbryla.game.yetanotherengine;

import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("cli")
public class Cli implements CommandLineRunner {

  private final StateStorage stateStorage;
  private final StateMachineFactory stateMachineFactory;

  @Autowired
  public Cli(StateStorage stateStorage, StateMachineFactory stateMachineFactory) {
    this.stateStorage = stateStorage;
    this.stateMachineFactory = stateMachineFactory;
  }

  @Override
  public void run(String... args) throws Exception {
    Presenter presenter = new ConsolePresenter(stateStorage);
    Random random = new Random();
    Operation operation = new AttackOperation(System.out::println);
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
    ArtificialIntelligence enemyAI = new ArtificialIntelligence(enemyFighter);
    StateMachine stateMachine = stateMachineFactory
        .createInMemoryStateMachine(subject -> random.nextInt(10));
    while (!stateMachine.isInTerminalState()) {
      presenter.showStatus();
      stateMachine.getNextSubject().ifPresent(subject -> {
            switch (subject.getName()) {
              case player1:
                stateMachine.execute(new Action(player1, enemy, operation));
                break;
              case player2:
                stateMachine.execute(new Action(player2, enemy, operation));
                break;
              case enemy:
                stateMachine.execute(enemyAI.nextAction(stateStorage));
            }
          }
      );
    }
    presenter.showStatus();

  }
}
