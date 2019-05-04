package dbryla.game.yetanotherengine.domain.encounters;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class MonstersBook {

  private final Map<Double, List<MonsterDefinition>> monsters;
  private final Random random;

  MonsterDefinition getRandomMonster(int playersLevel) {
    List<MonsterDefinition> possibleMonsters = monsters.keySet()
        .stream()
        .filter(challengeRating -> challengeRating <= playersLevel)
        .flatMap((Double challengeRating) -> monsters.get(challengeRating).stream())
        .collect(Collectors.toList());
    return possibleMonsters.get(random.nextInt(possibleMonsters.size()));
  }
}
