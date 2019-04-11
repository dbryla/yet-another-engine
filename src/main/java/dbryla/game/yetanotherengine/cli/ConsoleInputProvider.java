package dbryla.game.yetanotherengine.cli;

import static dbryla.game.yetanotherengine.domain.spells.SpellConstants.UNLIMITED_TARGETS;

import dbryla.game.yetanotherengine.InputProvider;
import dbryla.game.yetanotherengine.domain.Action;
import dbryla.game.yetanotherengine.domain.Game;
import dbryla.game.yetanotherengine.domain.IncorrectStateException;
import dbryla.game.yetanotherengine.domain.Instrument;
import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.operations.Operation;
import dbryla.game.yetanotherengine.domain.operations.SpellCastOperation;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subjects.Subject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Profile("cli")
public class ConsoleInputProvider implements InputProvider {

  private final ConsolePresenter presenter;
  private final BufferedReader input;
  private final Game game; //fixme
  private final EventHub eventHub;

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
  public void askForAction(Subject subject, Long gameId) {
    System.out.println(subject.getName() + " your turn!");
    List<Operation> availableOperations = presenter.showAvailableOperations(subject.getClass());
    int option = cmdLineToOption();
    Operation operation = availableOperations.get(option);
    Instrument instrument = getSpell(subject, operation)
        .map(Instrument::new)
        .orElse(new Instrument(subject.getEquipment().getWeapon()));
    int numberOfTargets = operation.getAllowedNumberOfTargets(instrument);
    boolean friendlyAction = isFriendlyAction(instrument);
    if (numberOfTargets == UNLIMITED_TARGETS) {
      game.move(new Action(subject.getName(), getAllTargets(friendlyAction), operation, instrument), eventHub);
    } else {
      game.move(new Action(subject.getName(), pickTargets(numberOfTargets, friendlyAction), operation, instrument), eventHub);
    }
  }

  private List<String> getAllTargets(boolean friendlyAction) {
    return friendlyAction ? game.getAllAliveAllies() : game.getAllAliveEnemies();
  }

  private Optional<Spell> getSpell(Subject subject, Operation operation) {
    if (operation instanceof SpellCastOperation) {
      List<Spell> spells = presenter.showAvailableSpells(subject.getClass());
      return Optional.of(spells.get(cmdLineToOption()));
    }
    return Optional.empty();
  }

  private boolean isFriendlyAction(Instrument instrument) {
    return instrument.getSpell() != null && instrument.getSpell().isPositiveSpell();
  }

  private List<String> pickTargets(int numberOfTargets, boolean friendlyTarget) {
    List<String> targets = new LinkedList<>();
    List<String> aliveTargets = getAllTargets(friendlyTarget);
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
    return availableTargets.get(cmdLineToOption());
  }
}
