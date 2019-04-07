package dbryla.game.yetanotherengine.domain;

import static dbryla.game.yetanotherengine.domain.GameOptions.ENEMIES;
import static dbryla.game.yetanotherengine.domain.GameOptions.ALLIES;

import dbryla.game.yetanotherengine.InputProvider;
import dbryla.game.yetanotherengine.domain.ai.ArtificialIntelligence;
import dbryla.game.yetanotherengine.domain.state.StateMachine;
import dbryla.game.yetanotherengine.domain.state.StateMachineFactory;
import dbryla.game.yetanotherengine.domain.state.storage.StateStorage;
import dbryla.game.yetanotherengine.domain.subjects.classes.Subject;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Weapon;
import dbryla.game.yetanotherengine.domain.subjects.classes.Fighter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Game {

  private final StateStorage stateStorage;
  private final StateMachineFactory stateMachineFactory;
  private final ArtificialIntelligence artificialIntelligence;
  private final InputProvider inputProvider;

  public void createCharacter(Subject subject) {
    stateStorage.save(subject);
  }

  public void createEnemies() {
    createEnemy("Orc", Weapon.SHORTSWORD);
    createEnemy("Goblin", Weapon.DAGGER);
  }

  private void createEnemy(String name, Weapon weapon) {
    Fighter enemy = Fighter.builder()
        .name(name)
        .affiliation(ENEMIES)
        .weapon(weapon)
        .abilities(new Abilities(10, 10, 10, 10, 10, 10))
        .build();
    stateStorage.save(enemy);
    artificialIntelligence.initSubject(enemy);
  }

  public List<String> getAllAliveEnemies() {
    return StreamSupport.stream(stateStorage.findAll().spliterator(), false)
        .filter(subject -> subject.getAffiliation().equals(ENEMIES) && !subject.isTerminated())
        .map(Subject::getName)
        .collect(Collectors.toUnmodifiableList());
  }

  public List<String> getAllAliveAllies() {
    return StreamSupport.stream(stateStorage.findAll().spliterator(), false)
        .filter(subject -> subject.getAffiliation().equals(ALLIES) && !subject.isTerminated())
        .map(Subject::getName)
        .collect(Collectors.toUnmodifiableList());
  }

  public void start() {
    StateMachine stateMachine = stateMachineFactory.createInMemoryStateMachine();
    while (!stateMachine.isInTerminalState()) {
      stateMachine.getNextSubject().ifPresent(subject -> {
            if (ALLIES.equals(subject.getAffiliation())) {
              stateMachine.execute(inputProvider.askForAction(subject, this));
            } else {
              stateMachine.execute(artificialIntelligence.attackAction(subject.getName()));
            }
          }
      );
    }
  }
}
