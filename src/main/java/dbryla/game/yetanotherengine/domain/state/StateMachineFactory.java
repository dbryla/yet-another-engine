package dbryla.game.yetanotherengine.domain.state;

import dbryla.game.yetanotherengine.domain.IncorrectStateException;
import dbryla.game.yetanotherengine.domain.Strategy;
import dbryla.game.yetanotherengine.domain.state.storage.InMemoryStepTracker;
import dbryla.game.yetanotherengine.domain.state.storage.StateStorage;
import dbryla.game.yetanotherengine.domain.state.storage.StepTracker;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class StateMachineFactory {

  private final StateStorage stateStorage;

  @Autowired
  public StateMachineFactory(StateStorage stateStorage) {
    this.stateStorage = stateStorage;
  }

  public StateMachine createInMemoryStateMachine(Strategy strategy) {
    if (strategy == null) {
      throw new IncorrectStateException("No strategy provided to state machine.");
    }
    List<SubjectIdentifier> subjectsForAction = StreamSupport
        .stream(stateStorage.findAll().spliterator(), false)
        .sorted(Comparator.comparingInt(strategy::calculateInitiative).reversed())
        .map(Subject::toIdentifier)
        .collect(Collectors.toList());
    Map<String, Long> affiliationMap = StreamSupport
        .stream(stateStorage.findAll().spliterator(), false)
        .collect(Collectors.groupingBy(Subject::getAffiliation, Collectors.counting()));
    StepTracker stepTracker = new InMemoryStepTracker(subjectsForAction, affiliationMap);
    return new DefaultStateMachine(stepTracker, stateStorage);
  }

}