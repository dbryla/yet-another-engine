package dbryla.game.yetanotherengine.domain;

import dbryla.game.yetanotherengine.domain.state.storage.StateStorage;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Game {

  private final StateStorage stateStorage;

  public void createCharacter(Subject subject) {
    stateStorage.save(subject);
  }
}
