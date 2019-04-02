package dbryla.game.yetanotherengine;

import dbryla.game.yetanotherengine.domain.Action;
import dbryla.game.yetanotherengine.domain.ai.ArtificialIntelligence;
import dbryla.game.yetanotherengine.domain.operations.AttackOperation;
import dbryla.game.yetanotherengine.domain.operations.Operation;
import dbryla.game.yetanotherengine.domain.state.StateMachine;
import dbryla.game.yetanotherengine.domain.state.StateMachineFactory;
import dbryla.game.yetanotherengine.domain.state.storage.StateStorage;
import dbryla.game.yetanotherengine.domain.subjects.Fighter;
import dbryla.game.yetanotherengine.domain.subjects.IncorrectAttributesException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("cli")
public class Cli implements CommandLineRunner {

  private static final String PLAYER = "player";
  private static final String ENEMIES = "enemies";
  private static final String ENEMY1 = "grey goblin";
  private static final String ENEMY2 = "green goblin";
  private final StateStorage stateStorage;
  private final StateMachineFactory stateMachineFactory;
  private final ArtificialIntelligence ai;
  private final Presenter presenter;
  private final Random random = new Random();
  private final Operation operation = new AttackOperation(System.out::println);
  private String playerName;

  public Cli(StateStorage stateStorage, StateMachineFactory stateMachineFactory, Presenter presenter) {
    this.stateStorage = stateStorage;
    this.stateMachineFactory = stateMachineFactory;
    this.presenter = presenter;
    ai = new ArtificialIntelligence(this.stateStorage, System.out::println);
  }

  @Override
  public void run(String... args) throws Exception {
    switch (args[0]) {
      case "sim":
        simulation();
        break;
      case "game":
        game();
        break;
    }

  }

  private void simulation() throws IncorrectAttributesException {
    System.out.println("Starting simulation...");
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
    ai.initSubject(enemyFighter);
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
                stateMachine.execute(ai.attackAction(enemy));
            }
          }
      );
    }
    presenter.showStatus();
  }

  private void game() throws IOException, IncorrectAttributesException {
    System.out.println("Starting game mode...");
    System.out.println("Type your character name and press enter to start.");
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    createPlayer(in);
    createEnemies();
    StateMachine stateMachine = stateMachineFactory
        .createInMemoryStateMachine(subject -> random.nextInt(20));
    while (!stateMachine.isInTerminalState()) {
      stateMachine.getNextSubject().ifPresent(subject -> {
            if (playerName.equals(subject.getName())) {
              presenter.showStatus();
              System.out.println("Your move! Type which enemy you want attack.");
              String target = readTarget(in);
              stateMachine.execute(new Action(playerName, target, operation));
            } else {
              stateMachine.execute(ai.attackAction(subject.getName()));
            }
          }
      );
    }
    System.out.println("The end.");
  }

  private void createPlayer(BufferedReader in) throws IOException {
    playerName = in.readLine();
    Fighter player = new Fighter(playerName, PLAYER);
    stateStorage.save(player);
  }

  private void createEnemies() throws IncorrectAttributesException {
    Fighter enemy1 = Fighter.builder()
        .name(ENEMY1)
        .affiliation(ENEMIES)
        .healthPoints(5)
        .build();
    stateStorage.save(enemy1);
    ai.initSubject(enemy1);
    Fighter enemy2 = Fighter.builder()
        .name(ENEMY2)
        .affiliation(ENEMIES)
        .healthPoints(5)
        .build();
    stateStorage.save(enemy2);
    ai.initSubject(enemy2);
  }

  private String readTarget(BufferedReader in) {
    try {
      return in.readLine();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
