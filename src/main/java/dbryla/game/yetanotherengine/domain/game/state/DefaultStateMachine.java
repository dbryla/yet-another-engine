package dbryla.game.yetanotherengine.domain.game.state;

import dbryla.game.yetanotherengine.domain.IncorrectStateException;
import dbryla.game.yetanotherengine.domain.events.Event;
import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.game.Action;
import dbryla.game.yetanotherengine.domain.game.SubjectTurn;
import dbryla.game.yetanotherengine.domain.game.state.storage.StateStorage;
import dbryla.game.yetanotherengine.domain.game.state.storage.StepTracker;
import dbryla.game.yetanotherengine.domain.operations.*;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static dbryla.game.yetanotherengine.domain.operations.OperationType.*;

@AllArgsConstructor
public class DefaultStateMachine implements StateMachine {

  private final Long gameId;
  private final StepTracker stepTracker;
  private final StateStorage stateStorage;
  private final EventHub eventHub;
  private final AttackOperation attackOperation;
  private final SpellCastOperation spellCastOperation;
  private final MoveOperation moveOperation;
  private final EffectConsumer effectConsumer;

  @Override
  public Optional<Subject> getNextSubject() {
    Optional<String> nextSubjectName = stepTracker.getNextSubjectName();
    if (nextSubjectName.isEmpty()) {
      return Optional.empty();
    }
    return stateStorage.findByIdAndName(gameId, nextSubjectName.get());
  }

  @Override
  public void execute(SubjectTurn subjectTurn) {
    getNextSubject().ifPresent(subject -> {
      verifySource(subjectTurn, subject);
      List<Event> events = subjectTurn.getActions()
          .stream()
          .flatMap(action -> invokeOperation(action, subject).stream())
          .collect(Collectors.toList());
      stepTracker.moveToNextSubject();
      events.addAll(apply(effectConsumer.apply(subject)));
      events.forEach(event -> eventHub.send(gameId, event));
    });
  }

  private List<Event> invokeOperation(Action action, Subject subject) {
    try {
      if (ATTACK.equals(action.getOperationType())) {
        return apply(attackOperation.invoke(subject, action.getActionData(), getTargets(action)));
      }
      if (SPELL_CAST.equals(action.getOperationType())) {
        return apply(spellCastOperation.invoke(subject, action.getActionData(), getTargets(action)));
      }
      if (MOVE.equals(action.getOperationType())) {
        return apply(moveOperation.invoke(subject, action.getActionData()));
      }
    } catch (UnsupportedGameOperationException e) {
      throw new IncorrectStateException("Couldn't invoke operation on target(s).", e);
    }
    throw new IncorrectStateException("Couldn't invoke operation " + action.getOperationType() + " on target(s).");
  }

  private void verifySource(SubjectTurn subjectTurn, Subject subject) {
    if (!subjectTurn.getOwnerName().equals(subject.getName())) {
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

  private List<Event> apply(OperationResult operationResult) {
    if (operationResult == null) {
      return List.of();
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
