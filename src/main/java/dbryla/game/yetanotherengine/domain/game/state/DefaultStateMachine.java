package dbryla.game.yetanotherengine.domain.game.state;

import dbryla.game.yetanotherengine.domain.Action;
import dbryla.game.yetanotherengine.domain.IncorrectStateException;
import dbryla.game.yetanotherengine.domain.events.Event;
import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.operations.AttackOperation;
import dbryla.game.yetanotherengine.domain.operations.OperationResult;
import dbryla.game.yetanotherengine.domain.operations.SpellCastOperation;
import dbryla.game.yetanotherengine.domain.operations.UnsupportedGameOperationException;
import dbryla.game.yetanotherengine.domain.game.state.storage.StateStorage;
import dbryla.game.yetanotherengine.domain.game.state.storage.StepTracker;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import lombok.AllArgsConstructor;

import java.util.Optional;
import java.util.Set;

import static dbryla.game.yetanotherengine.domain.operations.OperationType.ATTACK;
import static dbryla.game.yetanotherengine.domain.operations.OperationType.SPELL_CAST;

@AllArgsConstructor
public class DefaultStateMachine implements StateMachine {

  private final Long gameId;
  private final StepTracker stepTracker;
  private final StateStorage stateStorage;
  private final EventHub eventHub;
  private final AttackOperation attackOperation;
  private final SpellCastOperation spellCastOperation;

  @Override
  public Optional<Subject> getNextSubject() {
    Optional<String> nextSubjectName = stepTracker.getNextSubjectName();
    if (nextSubjectName.isEmpty()) {
      return Optional.empty();
    }
    return stateStorage.findByIdAndName(gameId, nextSubjectName.get());
  }

  @Override
  public void execute(Action action) {
    getNextSubject().ifPresent(subject -> {
      verifySource(action, subject);
      invokeOperation(action, subject)
          .forEach(event -> eventHub.send(gameId, event));
      stepTracker.moveToNextSubject();
    });
  }

  private Set<Event> invokeOperation(Action action, Subject subject) {
    try {
      if (ATTACK.equals(action.getOperationType())) {
        return apply(attackOperation.invoke(subject, action.getInstrument(), getTargets(action)));
      }
      if (SPELL_CAST.equals(action.getOperationType())) {
        return apply(spellCastOperation.invoke(subject, action.getInstrument(), getTargets(action)));
      }
    } catch (UnsupportedGameOperationException e) {
      throw new IncorrectStateException("Couldn't invoke operation on target(s).", e);
    }
    throw new IncorrectStateException("Couldn't invoke operation " + action.getOperationType() + " on target(s).");
  }

  private void verifySource(Action action, Subject subject) {
    if (!action.getSourceName().equals(subject.getName())) {
      throw new IncorrectStateException("Can't invoke action from different subject then next one.");
    }
  }

  private Subject[] getTargets(Action action) {
    return action.getTargetNames()
        .stream()
        .map((name) -> stateStorage.findByIdAndName(gameId, name))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .toArray(Subject[]::new);
  }

  private Set<Event> apply(OperationResult operationResult) {
    if (operationResult == null) {
      return Set.of();
    }
    operationResult.getChangedSubjects().forEach(subject -> {
      stateStorage.save(gameId, subject);
      if (subject.isTerminated()) {
        stepTracker.removeSubject(subject.toIdentifier());
      }
    });
    return operationResult.getEmittedEvents();
  }

  @Override
  public boolean isInTerminalState() {
    return stepTracker.hasNoActionsToTrack();
  }
}
