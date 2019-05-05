package dbryla.game.yetanotherengine.cli;

import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.encounters.MonstersFactory;
import dbryla.game.yetanotherengine.domain.game.Action;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.game.GameFactory;
import dbryla.game.yetanotherengine.domain.game.SubjectTurn;
import dbryla.game.yetanotherengine.domain.operations.ActionData;
import dbryla.game.yetanotherengine.domain.operations.OperationType;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

import static dbryla.game.yetanotherengine.domain.operations.OperationType.PASS;
import static dbryla.game.yetanotherengine.domain.operations.OperationType.STAND_UP;
import static dbryla.game.yetanotherengine.domain.spells.SpellConstants.ALL_TARGETS_WITHIN_RANGE;

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
      game.createPlayerCharacter(player);
    }
    game.createNonPlayableCharacters(monstersFactory.createEncounter(game.getPlayersNumber()));
    presenter.showStatus(gameId);
    game.start();
    while (!game.isEnded()) {
      game.getNextSubjectName().ifPresent(this::handleNextMove);
    }
    presenter.showStatus(gameId);
    log.info("The end.");
  }

  private void handleNextMove(String subjectName) {
    handleNextMove(subjectName, false);
  }

  private void handleNextMove(String subjectName, boolean isMoving) {
    Subject subject = game.getSubject(subjectName);
    OperationType operation = getOperation(subject, false);
    SubjectTurn subjectTurn = new SubjectTurn(subjectName);
    if (PASS.equals(operation)) {
      game.execute(subjectTurn);
      return;
    }
    if (STAND_UP.equals(operation)) {
      game.execute(subjectTurn.add(new Action(subjectName, STAND_UP)));
      operation = getOperation(subject, true);
    }
    ActionData actionData = getActionData(operation, subject);
    if (actionData.getPosition() == null) {
      int numberOfTargets = getAllowedNumberOfTargets(actionData);
      if (numberOfTargets == ALL_TARGETS_WITHIN_RANGE) {
        game.execute(subjectTurn.add(new Action(subjectName, game.getPossibleTargets(subject, actionData.getSpell()), operation, actionData)));
      } else {
        List<String> targets = pickTargets(subject, numberOfTargets, actionData);
        game.execute(subjectTurn.add(new Action(subjectName, targets, operation, actionData)));
      }
    } else {
      if (!isMoving) {
        game.moveSubject(subjectName, actionData.getPosition());
        System.out.println(subjectName + " moves to " + actionData.getPosition() + ".");
        handleNextMove(subjectName, true);
      } else {
        game.execute(subjectTurn.add(new Action(subjectName, operation, actionData)));
      }
    }
  }

  private OperationType getOperation(Subject subject, boolean stoodUp) {
    List<OperationType> availableOperations = presenter.showAvailableOperations(game, subject, stoodUp);
    int option = inputProvider.cmdLineToOption();
    return availableOperations.get(option);
  }

  private List<String> pickTargets(Subject subject, int numberOfTargets, ActionData actionData) {
    List<String> targets = new LinkedList<>();
    List<String> aliveTargets = getAliveTargets(subject, actionData);
    if (aliveTargets.size() <= numberOfTargets) {
      return aliveTargets;
    }
    for (int i = 0; i < numberOfTargets; i++) {
      targets.add(pickTarget(aliveTargets));
    }
    return targets;
  }

  private List<String> getAliveTargets(Subject subject, ActionData actionData) {
    if (actionData.getWeapon() != null) {
      return game.getPossibleTargets(subject, actionData.getWeapon());
    }
    if (actionData.getSpell() != null) {
      return game.getPossibleTargets(subject, actionData.getSpell());
    }
    return List.of();
  }

  private String pickTarget(List<String> aliveTargets) {
    presenter.showAvailableTargets(aliveTargets);
    return aliveTargets.get(inputProvider.cmdLineToOption());
  }

  private ActionData getActionData(OperationType operation, Subject subject) {
    if (OperationType.SPELL_CAST.equals(operation)) {
      List<Spell> spells = presenter.showAvailableSpells(game, subject);
      return new ActionData(spells.get(inputProvider.cmdLineToOption()));
    }
    if (OperationType.ATTACK.equals(operation)) {
      List<Weapon> weapons = presenter.showAvailableWeaponsToAttackWith(game, subject);
      return new ActionData(weapons.get(inputProvider.cmdLineToOption()));
    }
    if (OperationType.MOVE.equals(operation)) {
      presenter.showAvailablePositions(game, subject);
      return new ActionData(Position.valueOf(inputProvider.cmdLineToOption()));
    }
    throw new IllegalArgumentException("Unsupported operation type: " + operation);
  }

  private int getAllowedNumberOfTargets(ActionData actionData) {
    if (actionData.getSpell() == null) {
      return 1;
    }
    return actionData.getSpell().getMaximumNumberOfTargets();
  }
}
