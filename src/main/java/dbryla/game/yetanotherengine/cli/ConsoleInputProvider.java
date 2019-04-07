package dbryla.game.yetanotherengine.cli;

import static dbryla.game.yetanotherengine.domain.spells.SpellConstants.UNLIMITED_TARGETS;

import dbryla.game.yetanotherengine.InputProvider;
import dbryla.game.yetanotherengine.Presenter;
import dbryla.game.yetanotherengine.domain.Action;
import dbryla.game.yetanotherengine.domain.Game;
import dbryla.game.yetanotherengine.domain.IncorrectStateException;
import dbryla.game.yetanotherengine.domain.Instrument;
import dbryla.game.yetanotherengine.domain.operations.Operation;
import dbryla.game.yetanotherengine.domain.operations.SpellCastOperation;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subjects.classes.Subject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
    List<Operation> availableOperations = presenter.showAvailableOperations(subject.getClass());
    int option = cmdLineToOption();
    Operation operation = availableOperations.get(option);
    Instrument instrument = getSpell(subject, operation)
        .map(Instrument::new)
        .orElse(new Instrument(subject.getWeapon()));
    int numberOfTargets = operation.getAllowedNumberOfTargets(instrument);
    boolean friendlyAction = isFriendlyAction(instrument);
    if (numberOfTargets == UNLIMITED_TARGETS) {
      return new Action(subject.getName(), getAllTargets(game, friendlyAction), operation, instrument);
    }
    return new Action(subject.getName(), pickTargets(game, numberOfTargets, friendlyAction), operation, instrument);
  }

  private List<String> getAllTargets(Game game, boolean friendlyAction) {
    return friendlyAction ? game.getAllAliveFriends() : game.getAllAliveEnemies();
  }

  private boolean isFriendlyAction(Instrument instrument) {
    return instrument.getSpell() != null && instrument.getSpell().isPositiveSpell();
  }

  private Optional<Spell> getSpell(Subject subject, Operation operation) {
    if (operation instanceof SpellCastOperation) {
      List<Spell> spells = presenter.showAvailableSpells(subject.getClass());
      return Optional.of(spells.get(cmdLineToOption()));
    }
    return Optional.empty();
  }

  private List<String> pickTargets(Game game, int numberOfTargets, boolean friendlyAction) {
    List<String> targets = new LinkedList<>();
    List<String> aliveTargets = getAllTargets(game, friendlyAction);
    if (aliveTargets.size() <= numberOfTargets) {
      return aliveTargets;
    }
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
