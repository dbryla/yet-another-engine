package dbryla.game.yetanotherengine;

import java.util.List;
import java.util.Random;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("cli")
public class Cli implements CommandLineRunner {

  private final StateMachineFactory stateMachineFactory;

  @Autowired
  public Cli(StateMachineFactory stateMachineFactory) {
    this.stateMachineFactory = stateMachineFactory;
  }

  @Override
  public void run(String... args) throws Exception {
    Random random = new Random();
    Operation operation = new AttackOperation(System.out::println);
    String player1 = "Clemens";
    String player2 = "Maria";
    StateMachine stateMachine = stateMachineFactory
        .createInMemoryStateMachine(Set.of(new Fighter(player1), new Fighter(player2)), subject -> random.nextInt(10));
    while (!stateMachine.isInTerminalState()) {
      stateMachine.getNextSubject().ifPresent(subject -> {
            if (player1.equals(subject.getName())) {
              stateMachine.execute(new Action(player1, List.of(player2), operation));
            } else {
              stateMachine.execute(new Action(player2, List.of(player1), operation));
            }
          }
      );
      System.out.println(stateMachine.getSubjectsState());
    }
  }
}
