package dbryla.game.yetanotherengine.domain.game.state;

import dbryla.game.yetanotherengine.domain.IncorrectStateException;
import dbryla.game.yetanotherengine.domain.events.Event;
import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.game.Action;
import dbryla.game.yetanotherengine.domain.game.SubjectTurn;
import dbryla.game.yetanotherengine.domain.game.state.storage.StateStorage;
import dbryla.game.yetanotherengine.domain.game.state.storage.StepTracker;
import dbryla.game.yetanotherengine.domain.operations.EffectConsumer;
import dbryla.game.yetanotherengine.domain.operations.OperationFactory;
import dbryla.game.yetanotherengine.domain.operations.OperationResult;
import dbryla.game.yetanotherengine.domain.operations.UnsupportedGameOperationException;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
public class DefaultStateMachine implements StateMachine {

  private final Long gameId;
  private final StepTracker stepTracker;
  private final StateStorage stateStorage;
  private final EventHub eventHub;
  private final EffectConsumer effectConsumer;
  private final OperationFactory operationFactory;

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
          .flatMap(action -> invokeOperation(action).stream())
          .collect(Collectors.toList());
      stepTracker.moveToNextSubject();
      events.addAll(apply(effectConsumer.apply(subject)));
      events.forEach(event -> eventHub.send(gameId, event));
    });
  }

  private List<Event> invokeOperation(Action action) {
    Subject subject = getNextSubject().get();
    try {
      return apply(operationFactory.getOperation(action.getOperationType()).invoke(subject, action.getActionData(), getTargets(action)));
    } catch (UnsupportedGameOperationException e) {
      throw new IncorrectStateException("Couldn't invoke operation on target(s).", e);
    }
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
