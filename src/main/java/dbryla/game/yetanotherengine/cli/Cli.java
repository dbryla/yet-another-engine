package dbryla.game.yetanotherengine.cli;

import dbryla.game.yetanotherengine.domain.Game;
import dbryla.game.yetanotherengine.domain.GameFactory;
import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("cli")
@Slf4j
public class Cli implements CommandLineRunner {

  static final String SIMULATION_OPTION = "sim";
  static final String GAME_OPTION = "game";
  private final ConsolePresenter presenter;
  private final GameFactory gameFactory;
  private final ConsoleCharacterBuilder consoleCharacterBuilder;
  private final Simulator simulator;
  private final ConsoleInputProvider inputProvider;
  private final EventHub eventHub;

  public Cli(ConsolePresenter presenter,
      GameFactory gameFactory,
      ConsoleCharacterBuilder consoleCharacterBuilder,
      Simulator simulator,
      ConsoleInputProvider inputProvider,
      EventHub eventHub) {
    this.presenter = presenter;
    this.gameFactory = gameFactory;
    this.consoleCharacterBuilder = consoleCharacterBuilder;
    this.simulator = simulator;
    this.inputProvider = inputProvider;
    this.eventHub = eventHub;
  }

  @Override
  public void run(String... args) throws Exception {
    if (args.length >= 1) {
      switch (args[0]) {
        case SIMULATION_OPTION:
          simulation();
          break;
        case GAME_OPTION:
          game();
          break;
      }
    }
  }

  private void simulation() {
    log.info("Starting simulation...");
    simulator.start();
  }

  private void game() {
    log.info("Starting game mode...");
    Game game = gameFactory.newGame(123L);
    System.out.println("How many players want to join?");
    int playersNumber = inputProvider.cmdLineToOption();
    for (int i = 0; i < playersNumber; i++) {
      Subject player = consoleCharacterBuilder.createPlayer();
      game.createCharacter(player);
    }
    System.out.println("Do you want (0) random encounter or (1) specific one?");
    int option = inputProvider.cmdLineToOption();
    if (option == 1) {
      System.out.println("Which encounter do you want?");
      int encounterNumber = inputProvider.cmdLineToOption();
      game.createEnemies(playersNumber, encounterNumber);
    } else {
      game.createEnemies(playersNumber);
    }
    presenter.showStatus();
    game.start(eventHub);
    presenter.showStatus();
    log.info("The end.");
  }


}
