package dbryla.game.yetanotherengine.domain;

import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class AbilityScoresSupplier {

  public List<Integer> get() {
    List<Integer> abilityScores = new LinkedList<>();
    for (int i = 0; i < 6; i++) {
      List<Integer> rolls = new LinkedList<>();
      for (int j = 0; j < 4; j++) {
        rolls.add(DiceRoll.k6());
      }
      rolls.stream().sorted().skip(1).reduce(Integer::sum).ifPresent(abilityScores::add);
    }
    return abilityScores;
  }
}
