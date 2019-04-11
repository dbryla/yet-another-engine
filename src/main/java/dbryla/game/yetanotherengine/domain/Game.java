package dbryla.game.yetanotherengine.domain;

import static dbryla.game.yetanotherengine.domain.GameOptions.ALLIES;
import static dbryla.game.yetanotherengine.domain.GameOptions.ENEMIES;

import dbryla.game.yetanotherengine.domain.ai.ArtificialIntelligence;
import dbryla.game.yetanotherengine.domain.events.Event;
import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.operations.Operation;
import dbryla.game.yetanotherengine.domain.state.StateMachine;
import dbryla.game.yetanotherengine.domain.state.StateMachineFactory;
import dbryla.game.yetanotherengine.domain.state.storage.StateStorage;
import dbryla.game.yetanotherengine.domain.subjects.Monster;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;

@RequiredArgsConstructor
public class Game {

  @Getter
  private final Long id;
  private final StateStorage stateStorage;
  private final StateMachineFactory stateMachineFactory;
  private final ArtificialIntelligence artificialIntelligence;
  private final GameOptions gameOptions;

  private StateMachine stateMachine;

  public void createCharacter(Subject subject) {
    stateStorage.save(subject);
  }

  public void createEnemies(int playersNumber) {
    List<Monster> subjects = gameOptions.getRandomEncounter(playersNumber);
    createEnemies(subjects);
  }

  private void createEnemies(List<Monster> subjects) {
    subjects.forEach(subject -> {
      stateStorage.save(subject);
      artificialIntelligence.initSubject(subject);
    });
  }

  public void createEnemies(int playersNumber, int encounterNumber) {
    List<Monster> subjects = gameOptions.getEncounter(playersNumber, encounterNumber);
    createEnemies(subjects);
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

  @Async
  public void start(EventHub eventHub) {
    eventHub.send(new Event("You have approached hostile enemies.\n" + getAllAliveEnemies()), id);
    if (!isStarted()) {
      stateMachine = stateMachineFactory.createInMemoryStateMachine();
      gameLoop(eventHub);
    }
  }

  private void gameLoop(EventHub eventHub) {
    while (!stateMachine.isInTerminalState()) {
      if (stateMachine.getNextSubject().isPresent()) {
        Subject subject = stateMachine.getNextSubject().get();
        if (ALLIES.equals(subject.getAffiliation())) {
          eventHub.nextMove(subject, id);
          return;
        } else {
          stateMachine.execute(artificialIntelligence.action(subject.getName()))
              .forEach(event -> eventHub.send(event, id));
        }
      }
    }
  }

  @Async
  public void move(Action action, EventHub eventHub) {
    stateMachine.execute(action).forEach(event -> eventHub.send(event, id));
    gameLoop(eventHub);
  }

  public long getPlayersNumber() {
    return StreamSupport.stream(stateStorage.findAll().spliterator(), false)
        .filter(subject -> subject.getAffiliation().equals(ALLIES)).count();
  }

  public boolean isStarted() {
    return stateMachine != null && !stateMachine.isInTerminalState();
  }

  public Optional<String> getNextSubjectName() {
    return stateMachine.getNextSubject().map(Subject::getName);
  }

  public void attack(String playerName, Operation operation, EventHub eventHub) {
    move(new Action(playerName, getAllAliveEnemies().get(0), operation,
        new Instrument(stateStorage.findByName(playerName).get().getEquipment().getWeapon())), eventHub);
  }
}
