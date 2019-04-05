package dbryla.game.yetanotherengine.cli;

import dbryla.game.yetanotherengine.InputProvider;
import dbryla.game.yetanotherengine.Presenter;
import dbryla.game.yetanotherengine.domain.Action;
import dbryla.game.yetanotherengine.domain.Game;
import dbryla.game.yetanotherengine.domain.IncorrectStateException;
import dbryla.game.yetanotherengine.domain.operations.Operation;
import dbryla.game.yetanotherengine.domain.operations.SpellCastOperation;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.domain.subjects.classes.Mage;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
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
    Map<Integer, Operation> availableOperations = presenter.showAvailableOperations(subject);
    int option = cmdLineToOption();
    Operation operation = availableOperations.get(option);
    if (operation instanceof SpellCastOperation) {
      return castSpellAction((Mage) subject, game, operation);
    }
    return new Action(subject.getName(), pickTarget(game), operation);
  }

  private Action castSpellAction(Mage subject, Game game, Operation operation) {
    if (Spell.FIRE_BOLT.equals(subject.getSpell())) {
      return new Action(subject.getName(), pickTarget(game), operation);
    }
    return new Action(subject.getName(), game.getAllEnemies(), operation);
  }

  private String pickTarget(Game game) {
    Map<Integer, String> availableTargets = presenter.showAvailableTargets(game);
    return availableTargets.get(cmdLineToOption());
  }
}
