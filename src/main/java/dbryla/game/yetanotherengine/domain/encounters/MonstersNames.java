package dbryla.game.yetanotherengine.domain.encounters;

import dbryla.game.yetanotherengine.domain.subject.Race;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MonstersNames {

  List<String> getModifiers(Race race) {
    switch (race) {
      case HUMANOID:
        return List
            .of("Loony", "Clumsy", "Calm", "Arrogant", "Skinny", "Muscular", "Fat", "One-eyed", "Elderly", "Young", "Attractive", "Brawny", "Ugly");
      case GOBLINOID:
        return List.of("One-eyed", "Fat", "Stinky");
      case BEAST:
        return List.of("One-eyed", "Black");
      case UNDEAD:
        return List.of("Dusty", "Sturdy", "Brittle");
    }
    return List.of();
  }

}
