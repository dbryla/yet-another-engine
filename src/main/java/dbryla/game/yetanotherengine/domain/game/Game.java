package dbryla.game.yetanotherengine.domain.game;

import dbryla.game.yetanotherengine.domain.ai.ArtificialIntelligence;
import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.events.Event;
import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.game.state.StateMachine;
import dbryla.game.yetanotherengine.domain.game.state.StateMachineFactory;
import dbryla.game.yetanotherengine.domain.game.state.storage.StateStorage;
import dbryla.game.yetanotherengine.domain.encounters.SpecialAttack;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.Affiliation;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static dbryla.game.yetanotherengine.domain.battleground.Position.ENEMIES_BACK;
import static dbryla.game.yetanotherengine.domain.battleground.Position.PLAYERS_BACK;
import static dbryla.game.yetanotherengine.domain.subject.Affiliation.ENEMIES;
import static dbryla.game.yetanotherengine.domain.subject.Affiliation.PLAYERS;

@RequiredArgsConstructor
public class Game {

  @Getter
  private final Long id;
  private final StateStorage stateStorage;
  private final StateMachineFactory stateMachineFactory;
  private final ArtificialIntelligence artificialIntelligence;
  private final EventHub eventHub;

  private StateMachine stateMachine;

  public void createPlayerCharacter(Subject subject) {
    stateStorage.save(id, subject);
  }

  public void createNonPlayableCharacters(List<Subject> subjects) {
    subjects.forEach(subject -> {
      stateStorage.save(id, subject);
      artificialIntelligence.initSubject(this, subject);
    });
  }

  public List<String> getAllAliveAllyNames(Subject source) {
    return stateStorage.findAll(id).stream()
        .filter(subject -> source.getAffiliation().equals(subject.getAffiliation()) && !subject.isTerminated())
        .map(Subject::getName)
        .collect(Collectors.toUnmodifiableList());
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

  public List<String> getAllAliveEnemyNames() {
    return stateStorage.findAll(id).stream()
        .filter(subject -> ENEMIES.equals(subject.getAffiliation()) && !subject.isTerminated())
        .map(Subject::getName)
        .collect(Collectors.toUnmodifiableList());
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
    if (stateMachine == null) {
      return Optional.empty();
    }
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
        .filter(subject -> !subject.isTerminated())
        .collect(Collectors.groupingBy(Subject::getPosition));
  }

  public void moveSubject(String playerName, Position newPosition) {
    stateStorage.findByIdAndName(id, playerName).ifPresent(subject -> stateStorage.save(id, subject.of(newPosition)));
  }

  public List<String> getPossibleTargets(String subjectName, Weapon weapon) {
    return getPossibleTargets(getSubject(subjectName), weapon);
  }

  public List<String> getPossibleTargets(Subject subject, Weapon weapon) {
    return getPossibleTargets(subject, weapon.getMinRange(), weapon.getMaxRange(), false);
  }

  public List<String> getPossibleTargets(String subjectName, Spell spell) {
    return getPossibleTargets(getSubject(subjectName), spell);
  }

  public List<String> getPossibleTargets(Subject subject, Spell spell) {
    return getPossibleTargets(subject, spell.getMinRange(), spell.getMaxRange(), spell.isPositiveSpell());
  }

  public List<String> getPossibleTargets(Subject subject, SpecialAttack specialAttack) {
    return getPossibleTargets(subject, specialAttack.getMinRange(), specialAttack.getMaxRange(), false);
  }

  private List<String> getPossibleTargets(Subject subject, int minRange, int maxRange, boolean allies) {
    Affiliation targetsAffiliation = allies ? subject.getAffiliation() : getEnemyAffiliation(subject.getAffiliation());
    int position = subject.getPosition().getBattlegroundLocation();
    Map<Position, List<Subject>> positionsMap = getSubjectsPositionsMap();
    return getPositionsStream(subject, minRange, maxRange, position)
        .mapToObj(battlegroundPosition -> positionsMap.getOrDefault(Position.valueOf(battlegroundPosition), List.of())
            .stream()
            .filter(target -> targetsAffiliation.equals(target.getAffiliation()) && !target.isTerminated())
            .map(Subject::getName))
        .flatMap(Function.identity())
        .collect(Collectors.toList());
  }

  private IntStream getPositionsStream(Subject subject, int minRange, int maxRange, int position) {
    if (PLAYERS.equals(subject.getAffiliation())) {
      return IntStream.range(position + minRange, Math.min(ENEMIES_BACK.getBattlegroundLocation(), position + maxRange) + 1);
    } else {
      return IntStream.range(Math.max(PLAYERS_BACK.getBattlegroundLocation(), position - maxRange), position - minRange + 1);
    }
  }

  private Affiliation getEnemyAffiliation(Affiliation affiliation) {
    return PLAYERS.equals(affiliation) ? ENEMIES : PLAYERS;
  }

  public boolean areEnemiesOnCurrentPosition(Subject subject) {
    return getSubjectsPositionsMap()
        .getOrDefault(subject.getPosition(), List.of())
        .stream()
        .anyMatch(anySubject -> anySubject.getAffiliation().equals(getEnemyAffiliation(subject.getAffiliation())));
  }

  public List<Weapon> getAvailableWeaponsForAttack(Subject subject) {
    return subject.getEquipment()
        .getWeapons()
        .stream()
        .filter(weapon -> !getPossibleTargets(subject, weapon).isEmpty())
        .collect(Collectors.toList());
  }

  public List<Spell> getAvailableSpellsForCast(Subject subject) {
    List<Spell> spells = Arrays.stream(Spell.values())
        .filter(spell -> spell.forClass(subject.getCharacterClass()))
        .collect(Collectors.toList());
    spells.addAll(subject.getSpells());
    return spells.stream().filter(spell -> !getPossibleTargets(subject, spell).isEmpty()).collect(Collectors.toList());
  }
}
