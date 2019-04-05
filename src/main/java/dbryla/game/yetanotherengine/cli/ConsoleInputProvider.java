package dbryla.game.yetanotherengine.cli;

import static dbryla.game.yetanotherengine.domain.spells.SpellConstants.UNLIMITED_TARGETS;

import dbryla.game.yetanotherengine.InputProvider;
import dbryla.game.yetanotherengine.Presenter;
import dbryla.game.yetanotherengine.domain.Action;
import dbryla.game.yetanotherengine.domain.Game;
import dbryla.game.yetanotherengine.domain.IncorrectStateException;
import dbryla.game.yetanotherengine.domain.operations.Operation;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ConsoleInputProvider implements InputProvider {

  private final Presenter presenter;
  private final BufferedReader input;

  int cmdLineToOption() {
    try {
      return Integer.valueOf(input.readLine());
    } catch (IOException e) {
      throw new IncorrectStateException("Exception while reading cmdline option.", e);
    }
  }

  String cmdLine() {
    try {
      return input.readLine();
    } catch (IOException e) {
      throw new IncorrectStateException("Exception while reading cmdline option.", e);
    }
  }

  @Override
  public Action askForAction(Subject subject, Game game) {
    List<Operation> availableOperations = presenter.showAvailableOperations(subject);
    int option = cmdLineToOption();
    Operation operation = availableOperations.get(option);
    int numberOfTargets = operation.getAllowedNumberOfTargets(subject);
    if (numberOfTargets == UNLIMITED_TARGETS) {
      return new Action(subject.getName(), game.getAllAliveEnemies(), operation);
    }
    return new Action(subject.getName(), pickTargets(game, numberOfTargets), operation);
  }

  private List<String> pickTargets(Game game, int numberOfTargets) {
    List<String> targets = new LinkedList<>();
    for (int i = 0; i < numberOfTargets; i++) {
      targets.add(pickTarget(game));
    }
    return targets;
  }

  private String pickTarget(Game game) {
    List<String> availableTargets = presenter.showAvailableTargets(game);
    return availableTargets.get(cmdLineToOption());
  }
}
