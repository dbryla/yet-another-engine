package dbryla.game.yetanotherengine.domain;

import dbryla.game.yetanotherengine.domain.ai.ArtificialIntelligence;
import dbryla.game.yetanotherengine.domain.state.storage.StateStorage;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.domain.subjects.Weapon;
import dbryla.game.yetanotherengine.domain.subjects.classes.Fighter;
import lombok.AllArgsConstructor;

import static dbryla.game.yetanotherengine.domain.GameOptions.ENEMIES;

@AllArgsConstructor
public class Game {

  private final StateStorage stateStorage;
  private final ArtificialIntelligence artificialIntelligence;

  public void createCharacter(Subject subject) {
    stateStorage.save(subject);
  }

  public void createEnemies() {
    createEnemy("Orc", 8, Weapon.SHORTSWORD);
    createEnemy("Goblin", 4, Weapon.DAGGER);
  }

  private void createEnemy(String name, int healthPoints, Weapon weapon) {
    Fighter enemy = Fighter.builder()
        .name(name)
        .affiliation(ENEMIES)
        .healthPoints(healthPoints)
        .weapon(weapon)
        .build();
    stateStorage.save(enemy);
    artificialIntelligence.initSubject(enemy);
  }
}
