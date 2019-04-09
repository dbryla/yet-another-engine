package dbryla.game.yetanotherengine.domain;

import static dbryla.game.yetanotherengine.domain.GameOptions.ALLIES;
import static dbryla.game.yetanotherengine.domain.GameOptions.ENEMIES;

import dbryla.game.yetanotherengine.InputProvider;
import dbryla.game.yetanotherengine.domain.ai.ArtificialIntelligence;
import dbryla.game.yetanotherengine.domain.state.StateMachine;
import dbryla.game.yetanotherengine.domain.state.StateMachineFactory;
import dbryla.game.yetanotherengine.domain.state.storage.StateStorage;
import dbryla.game.yetanotherengine.domain.subjects.Monster;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
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
  private final GameOptions gameOptions;

  public void createCharacter(Subject subject) {
    stateStorage.save(subject);
  }

  public void createEnemies(int playersNumber) {
    List<Monster> subjects = gameOptions.getRandomEncounter(playersNumber);
    subjects.forEach(subject -> {
      stateStorage.save(subject);
      artificialIntelligence.initSubject(subject);
    });
  }

  public void createEnemies(int playersNumber, int encounterNumber) {
    List<Monster> subjects = gameOptions.getEncounter(playersNumber, encounterNumber);
    subjects.forEach(subject -> {
      stateStorage.save(subject);
      artificialIntelligence.initSubject(subject);
    });

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
              stateMachine.execute(artificialIntelligence.action(subject.getName()));
            }
          }
      );
    }
  }
}
