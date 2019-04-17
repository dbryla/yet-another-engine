package dbryla.game.yetanotherengine.cli;

import static dbryla.game.yetanotherengine.domain.spells.SpellConstants.UNLIMITED_TARGETS;

import dbryla.game.yetanotherengine.domain.game.Action;
import dbryla.game.yetanotherengine.domain.encounters.MonstersFactory;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.game.GameFactory;
import dbryla.game.yetanotherengine.domain.game.SubjectTurn;
import dbryla.game.yetanotherengine.domain.operations.ActionData;
import dbryla.game.yetanotherengine.domain.operations.OperationType;
import dbryla.game.yetanotherengine.domain.spells.Spell;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import dbryla.game.yetanotherengine.domain.subject.Subject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("cli")
@Slf4j
@Primary
@RequiredArgsConstructor
public class Cli implements CommandLineRunner {

  static final String SIMULATION_OPTION = "sim";
  static final String GAME_OPTION = "game";
  private final ConsolePresenter presenter;
  private final GameFactory gameFactory;
  private final ConsoleCharacterBuilder consoleCharacterBuilder;
  private final Simulator simulator;
  private final ConsoleInputProvider inputProvider;
  private final MonstersFactory monstersFactory;
  private Game game;

  @Override
  public void run(String... args) {
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
    long gameId = 123L;
    game = gameFactory.newGame(gameId);
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
      game.createEnemies(monstersFactory.createEncounter(game.getPlayersNumber(), encounterNumber));
    } else {
      game.createEnemies(monstersFactory.createEncounter(game.getPlayersNumber()));
    }
    presenter.showStatus(gameId);
    game.start();
    while (!game.isEnded()) {
      game.getNextSubjectName().ifPresent(this::handleNextMove);
    }
    presenter.showStatus(gameId);
    log.info("The end.");
  }

  private Optional<Spell> getSpell(Subject subject, OperationType operation) {
    if (OperationType.SPELL_CAST.equals(operation)) {
      List<Spell> spells = presenter.showAvailableSpells(subject.getCharacterClass());
      return Optional.of(spells.get(inputProvider.cmdLineToOption()));
    }
    return Optional.empty();
  }

  private boolean isFriendlyAction(ActionData actionData) {
    return actionData.getSpell() != null && actionData.getSpell().isPositiveSpell();
  }

  private List<String> pickTargets(int numberOfTargets, boolean friendlyTarget) {
    List<String> targets = new LinkedList<>();
    List<String> aliveTargets = game.getAllAliveSubjectNames(friendlyTarget);
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

  private void handleNextMove(String subjectName) {
    Subject subject = game.getSubject(subjectName);
    List<OperationType> availableOperations = presenter.showAvailableOperations(subject.getCharacterClass());
    int option = inputProvider.cmdLineToOption();
    OperationType operation = availableOperations.get(option);
    ActionData actionData = getSpell(subject, operation)
        .map(ActionData::new)
        .orElse(new ActionData(subject.getEquipment().getWeapons().get(0))); // fixme choose weapon from player
    int numberOfTargets = getAllowedNumberOfTargets(actionData);
    boolean friendlyAction = isFriendlyAction(actionData);
    if (numberOfTargets == UNLIMITED_TARGETS) {
      game.execute(SubjectTurn.of(new Action(subject.getName(), game.getAllAliveSubjectNames(friendlyAction), operation, actionData)));
    } else {
      game.execute(SubjectTurn.of(new Action(subject.getName(), pickTargets(numberOfTargets, friendlyAction), operation, actionData)));
    }
  }

  private int getAllowedNumberOfTargets(ActionData actionData) {
    if (actionData.getSpell() == null) {
      return 1;
    }
    return actionData.getSpell().getMaximumNumberOfTargets();
  }
}
