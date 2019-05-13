package dbryla.game.yetanotherengine.domain.game.state;

import dbryla.game.yetanotherengine.domain.dice.DiceRollService;
import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.game.state.storage.InMemoryStepTracker;
import dbryla.game.yetanotherengine.domain.game.state.storage.SubjectStorage;
import dbryla.game.yetanotherengine.domain.game.state.storage.StepTracker;
import dbryla.game.yetanotherengine.domain.operations.*;
import dbryla.game.yetanotherengine.domain.subject.Affiliation;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class StateMachineFactory {

  private final SubjectStorage subjectStorage;
  private final EventHub eventHub;
  private final OperationFactory operationFactory;
  private final DiceRollService diceRollService;
  private final EffectConsumer effectConsumer;

  public StateMachine createInMemoryStateMachine(Long gameId) {
    Map<Subject, Integer> initiatives = subjectStorage.findAll(gameId).stream()
        .collect(Collectors.toMap(Function.identity(), subject -> diceRollService.k20() + subject.getInitiativeModifier()));
    List<String> subjectsForAction = subjectStorage.findAll(gameId).stream()
        .sorted(Comparator.comparingInt(initiatives::get).reversed())
        .map(Subject::getName)
        .collect(Collectors.toList());
    Map<Affiliation, Long> affiliationMap = subjectStorage.findAll(gameId).stream()
        .collect(Collectors.groupingBy(Subject::getAffiliation, Collectors.counting()));
    StepTracker stepTracker = new InMemoryStepTracker(subjectsForAction, affiliationMap);
    return new DefaultStateMachine(gameId, stepTracker, subjectStorage, eventHub, effectConsumer, operationFactory);
  }

}
