package dbryla.game.yetanotherengine.domain.game;

import static dbryla.game.yetanotherengine.domain.game.GameOptions.PLAYERS;
import static dbryla.game.yetanotherengine.domain.game.GameOptions.ENEMIES;

import dbryla.game.yetanotherengine.domain.ai.ArtificialIntelligence;
import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.events.Event;
import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.game.state.StateMachine;
import dbryla.game.yetanotherengine.domain.game.state.StateMachineFactory;
import dbryla.game.yetanotherengine.domain.game.state.storage.StateStorage;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import dbryla.game.yetanotherengine.domain.subject.Subject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Game {

  @Getter
  private final Long id;
  private final StateStorage stateStorage;
  private final StateMachineFactory stateMachineFactory;
  private final ArtificialIntelligence artificialIntelligence;
  private final EventHub eventHub;

  private StateMachine stateMachine;

  public void createCharacter(Subject subject) {
    stateStorage.save(id, subject);
  }

  public void createEnemies(List<Subject> subjects) {
    subjects.forEach(subject -> {
      stateStorage.save(id, subject);
      artificialIntelligence.initSubject(id, subject);
    });
  }

  public List<String> getAllAliveEnemyNames() {
    return stateStorage.findAll(id).stream()
        .filter(subject -> subject.getAffiliation().equals(ENEMIES) && !subject.isTerminated())
        .map(Subject::getName)
        .collect(Collectors.toUnmodifiableList());
  }

  public List<String> getAllAliveAllyNames() {
    return stateStorage.findAll(id).stream()
        .filter(subject -> subject.getAffiliation().equals(PLAYERS) && !subject.isTerminated())
        .map(Subject::getName)
        .collect(Collectors.toUnmodifiableList());
  }

  public List<String> getAllAliveSubjectNames(boolean allies) {
    return allies ? getAllAliveAllyNames() : getAllAliveEnemyNames();
  }

  public List<Subject> getAllSubjects() {
    return stateStorage.findAll(id);
  }

  public boolean isStarted() {
    return stateMachine != null;
  }

  public void start() {
    eventHub.send(id, new Event("You have approached hostile enemies: " + getAllAliveEnemyNames()));
    if (!isStarted()) {
      stateMachine = stateMachineFactory.createInMemoryStateMachine(id);
      gameLoop();
    }
  }

  private void gameLoop() {
    while (!stateMachine.isInTerminalState()) {
      if (stateMachine.getNextSubject().isPresent()) {
        Subject subject = stateMachine.getNextSubject().get();
        if (PLAYERS.equals(subject.getAffiliation())) {
          eventHub.notifySubjectAboutNextMove(id, subject);
          return;
        } else {
          stateMachine.execute(artificialIntelligence.action(subject.getName()));
        }
      }
    }
  }

  public void execute(SubjectTurn subjectTurn) {
    stateMachine.execute(subjectTurn);
    gameLoop();
  }

  public Optional<String> getNextSubjectName() {
    return stateMachine.getNextSubject().map(Subject::getName);
  }

  public Subject getSubject(String subjectName) {
    return stateStorage.findByIdAndName(id, subjectName).orElse(null);
  }

  public void cleanup() {
    stateStorage.deleteAll(id);
  }

  public boolean isEnded() {
    return stateMachine.isInTerminalState();
  }

  public int getPlayersNumber() {
    return (int) stateStorage.findAll(id).stream().filter(subject -> PLAYERS.equals(subject.getAffiliation())).count();
  }

  public Map<Position, List<Subject>> getSubjectsPositionsMap() {
    return stateStorage.findAll(id)
        .stream()
        .collect(Collectors.groupingBy(Subject::getPosition));
  }

  public void moveSubject(String playerName, Position newPosition) {
    stateStorage.findByIdAndName(id, playerName).ifPresent(subject -> stateStorage.save(id, subject.of(newPosition)));
  }
}
