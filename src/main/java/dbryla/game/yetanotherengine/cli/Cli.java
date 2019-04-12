package dbryla.game.yetanotherengine.cli;

import static dbryla.game.yetanotherengine.domain.spells.SpellConstants.UNLIMITED_TARGETS;

import dbryla.game.yetanotherengine.domain.Action;
import dbryla.game.yetanotherengine.domain.Game;
import dbryla.game.yetanotherengine.domain.GameFactory;
import dbryla.game.yetanotherengine.domain.Instrument;
import dbryla.game.yetanotherengine.domain.events.Event;
import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.events.LoggingEventHub;
import dbryla.game.yetanotherengine.domain.operations.Operation;
import dbryla.game.yetanotherengine.domain.operations.SpellCastOperation;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("cli")
@Slf4j
@Primary
public class Cli implements CommandLineRunner, EventHub {

  static final String SIMULATION_OPTION = "sim";
  static final String GAME_OPTION = "game";
  private final ConsolePresenter presenter;
  private final GameFactory gameFactory;
  private final ConsoleCharacterBuilder consoleCharacterBuilder;
  private final Simulator simulator;
  private final ConsoleInputProvider inputProvider;
  private final LoggingEventHub loggingEventHub;
  private Game game;

  public Cli(ConsolePresenter presenter,
      GameFactory gameFactory,
      ConsoleCharacterBuilder consoleCharacterBuilder,
      Simulator simulator,
      ConsoleInputProvider inputProvider,
      LoggingEventHub loggingEventHub) {
    this.presenter = presenter;
    this.gameFactory = gameFactory;
    this.consoleCharacterBuilder = consoleCharacterBuilder;
    this.simulator = simulator;
    this.inputProvider = inputProvider;
    this.loggingEventHub = loggingEventHub;
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
    simulator.start(this);
  }

  private void game() {
    log.info("Starting game mode...");
    game = gameFactory.newGame(123L);
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
    game.start(this);
    presenter.showStatus();
    log.info("The end.");
  }

  private Optional<Spell> getSpell(Subject subject, Operation operation) {
    if (operation instanceof SpellCastOperation) {
      List<Spell> spells = presenter.showAvailableSpells(subject.getClass());
      return Optional.of(spells.get(inputProvider.cmdLineToOption()));
    }
    return Optional.empty();
  }

  private boolean isFriendlyAction(Instrument instrument) {
    return instrument.getSpell() != null && instrument.getSpell().isPositiveSpell();
  }

  private List<String> pickTargets(int numberOfTargets, boolean friendlyTarget) {
    List<String> targets = new LinkedList<>();
    List<String> aliveTargets = game.getAllAlive(friendlyTarget);
    if (aliveTargets.size() <= numberOfTargets) {
      return aliveTargets;
    }
    for (int i = 0; i < numberOfTargets; i++) {
      targets.add(pickTarget(friendlyTarget));
    }
    return targets;
  }

  private String pickTarget(boolean friendlyTarget) {
    List<String> availableTargets = friendlyTarget
        ? presenter.showAvailableFriendlyTargets(game)
        : presenter.showAvailableEnemyTargets(game);
    return availableTargets.get(inputProvider.cmdLineToOption());
  }

  @Override
  public void send(Event event, Long gameId) {
    loggingEventHub.send(event, gameId);
  }

  @Override
  public void nextMove(Subject subject, Long gameId) {
    log.info("{} your turn!", subject.getName());
    List<Operation> availableOperations = presenter.showAvailableOperations(subject.getClass());
    int option = inputProvider.cmdLineToOption();
    Operation operation = availableOperations.get(option);
    Instrument instrument = getSpell(subject, operation)
        .map(Instrument::new)
        .orElse(new Instrument(subject.getEquipment().getWeapon()));
    int numberOfTargets = operation.getAllowedNumberOfTargets(instrument);
    boolean friendlyAction = isFriendlyAction(instrument);
    if (numberOfTargets == UNLIMITED_TARGETS) {
      game.move(new Action(subject.getName(), game.getAllAlive(friendlyAction), operation, instrument), this);
    } else {
      game.move(new Action(subject.getName(), pickTargets(numberOfTargets, friendlyAction), operation, instrument), this);
    }
  }
}
