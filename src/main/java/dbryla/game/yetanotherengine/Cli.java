package dbryla.game.yetanotherengine;

import java.util.List;
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
    Random random = new Random();
    Operation operation = new AttackOperation(System.out::println);
    String player1 = "Clemens";
    String player2 = "Maria";
    stateStorage.save(new Fighter(player1, "blue"));
    stateStorage.save(new Fighter(player2, "green"));
    StateMachine stateMachine = stateMachineFactory
        .createInMemoryStateMachine(subject -> random.nextInt(10));
    while (!stateMachine.isInTerminalState()) {
      stateMachine.getNextSubject().ifPresent(subject -> {
            if (player1.equals(subject.getName())) {
              stateMachine.execute(new Action(player1, List.of(player2), operation));
            } else {
              stateMachine.execute(new Action(player2, List.of(player1), operation));
            }
          }
      );
      System.out.println(stateStorage.findAll());
    }
  }
}
