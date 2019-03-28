package dbryla.game.yetanotherengine;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class StateMachineFactory {

  public StateMachine createInMemoryStateMachine(Set<Subject> subjects, Strategy strategy) {
    if (subjects == null || subjects.isEmpty()) {
      throw new IncorrectStateException("No subjects provided to state machine.");
    }
    if (strategy == null) {
      throw new IncorrectStateException("No strategy provided to state machine.");
    }
    List<String> subjectsForAction = subjects.stream()
        .sorted(Comparator.comparingInt(strategy::calculateInitiative).reversed())
        .map(Subject::getName)
        .collect(Collectors.toList());
    Map<String, Subject> subjectsState = subjects.stream()
        .collect(Collectors.toMap(Subject::getName, Function.identity()));
    return new InMemoryStateMachine(subjectsForAction, subjectsState);
  }

}
