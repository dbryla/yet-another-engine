package dbryla.game.yetanotherengine.domain;

import dbryla.game.yetanotherengine.domain.subjects.Subject;
import org.springframework.stereotype.Component;

@Component
public class Strategy {

  public int calculateInitiative(Subject subject) {
    return DiceRoll.k20() + subject.getInitiativeModifier();
  }
}
