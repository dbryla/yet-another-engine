package dbryla.game.yetanotherengine;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ConsolePresenter implements Presenter {

  private final StateStorage stateStorage;

  @Override
  public void showStatus() {
    StreamSupport.stream(stateStorage.findAll().spliterator(), false)
        .collect(Collectors.groupingBy(Subject::getAffiliation))
        .forEach((team, subjects) ->
            subjects.stream()
                .filter(subject -> subject.getHealthPoints() > 0)
                .map(subject -> subject.getName() + "(" + subject.getHealthPoints() + ")")
                .reduce((left, right) -> left + " " + right)
                .ifPresent(status -> System.out.print("| " + status + " |")));
    System.out.println();
  }
}
