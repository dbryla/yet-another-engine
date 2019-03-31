package dbryla.game.yetanotherengine;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    List<String> subjectsForAction = StreamSupport
        .stream(stateStorage.findAll().spliterator(), false)
        .sorted(Comparator.comparingInt(strategy::calculateInitiative).reversed())
        .map(Subject::getName)
        .collect(Collectors.toList());
    return new InMemoryStateMachine(subjectsForAction, stateStorage);
  }

}
