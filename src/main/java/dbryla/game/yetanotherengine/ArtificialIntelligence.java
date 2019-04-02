package dbryla.game.yetanotherengine;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ArtificialIntelligence {

  private final Subject subject;
  private String acquiredTarget;

  public Action nextAction(StateStorage stateStorage) {
    setTarget(stateStorage);
    return new Action(subject.getName(), acquiredTarget, new AttackOperation(System.out::println));
  }

  private void setTarget(StateStorage stateStorage) {
    if (acquiredTarget == null) {
      findNewTarget(stateStorage);
    } else {
      Optional<Subject> target = stateStorage.findByName(acquiredTarget);
      if (target.isEmpty()) {
        throw new IncorrectStateException("State storage is corrupted. Can't find subject: " + acquiredTarget);
      }
      if (target.get().isTerminated()) {
        findNewTarget(stateStorage);
      }
    }
  }

  private void findNewTarget(StateStorage stateStorage) {
    Optional<Subject> target = StreamSupport.stream(stateStorage.findAll().spliterator(), false)
        .filter(s -> !s.isTerminated() && !s.getAffiliation().equals(subject.getAffiliation()))
        .min(Comparator.comparingInt(Subject::getHealthPoints));
    acquiredTarget = target.map(Subject::getName)
        .orElseThrow(() -> new IncorrectStateException("Target not found for subject: " + subject.getName()));
  }
}
