package dbryla.game.yetanotherengine.domain.state;

import dbryla.game.yetanotherengine.domain.Strategy;
import dbryla.game.yetanotherengine.domain.state.storage.InMemoryStepTracker;
import dbryla.game.yetanotherengine.domain.state.storage.StateStorage;
import dbryla.game.yetanotherengine.domain.state.storage.StepTracker;
import dbryla.game.yetanotherengine.domain.subjects.Subject;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class StateMachineFactory {

  private final StateStorage stateStorage;
  private final Strategy strategy;

  public StateMachine createInMemoryStateMachine() {
    Map<Subject, Integer> initiatives = StreamSupport
        .stream(stateStorage.findAll().spliterator(), false)
        .collect(Collectors.toMap(Function.identity(), strategy::calculateInitiative));
    List<SubjectIdentifier> subjectsForAction = StreamSupport
        .stream(stateStorage.findAll().spliterator(), false)
        .sorted(Comparator.comparingInt(initiatives::get).reversed())
        .map(Subject::toIdentifier)
        .collect(Collectors.toList());
    Map<String, Long> affiliationMap = StreamSupport
        .stream(stateStorage.findAll().spliterator(), false)
        .collect(Collectors.groupingBy(Subject::getAffiliation, Collectors.counting()));
    StepTracker stepTracker = new InMemoryStepTracker(subjectsForAction, affiliationMap);
    return new DefaultStateMachine(stepTracker, stateStorage);
  }

}
