package dbryla.game.yetanotherengine.domain.ai;

import dbryla.game.yetanotherengine.domain.Action;
import dbryla.game.yetanotherengine.domain.IncorrectStateException;
import dbryla.game.yetanotherengine.domain.operations.Operation;
import dbryla.game.yetanotherengine.domain.state.storage.StateStorage;
import dbryla.game.yetanotherengine.domain.subjects.classes.Subject;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.springframework.stereotype.Component;

@Component
public class ArtificialIntelligence {

  private final Map<String, ArtificialIntelligenceConfiguration> subjects = new HashMap<>();
  private final StateStorage stateStorage;
  private final Operation attackOperation;

  public ArtificialIntelligence(StateStorage stateStorage, Operation attackOperation) {
    this.stateStorage = stateStorage;
    this.attackOperation = attackOperation;
  }

  public void initSubject(Subject subject) {
    subjects.put(subject.getName(), new ArtificialIntelligenceConfiguration(subject));
  }

  public Action attackAction(String subjectName) {
    if (!subjects.containsKey(subjectName)) {
      throw new IncorrectStateException("Subject " + subjectName + " isn't initialized with AI.");
    }
    ArtificialIntelligenceConfiguration ai = subjects.get(subjectName);
    setTarget(ai);
    return new Action(subjectName, ai.getAcquiredTarget(), attackOperation);
  }

  private void setTarget(ArtificialIntelligenceConfiguration ai) {
    String acquiredTarget = ai.getAcquiredTarget();
    if (acquiredTarget == null) {
      ai.setAcquiredTarget(findNewTarget(ai.getSubject()));
    } else {
      Optional<Subject> target = stateStorage.findByName(acquiredTarget);
      if (target.isEmpty()) {
        throw new IncorrectStateException("State storage is corrupted. Can't find subject: " + acquiredTarget);
      }
      if (target.get().isTerminated()) {
        ai.setAcquiredTarget(findNewTarget(ai.getSubject()));
      }
    }
  }

  private String findNewTarget(Subject subject) {
    Optional<Subject> target = StreamSupport.stream(stateStorage.findAll().spliterator(), false)
        .filter(s -> !s.isTerminated() && !s.getAffiliation().equals(subject.getAffiliation()))
        .min(Comparator.comparingInt(Subject::getHealthPoints));
    return target.map(Subject::getName)
        .orElseThrow(() -> new IncorrectStateException("Target not found for subject: " + subject.getName()));
  }
}
