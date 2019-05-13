package dbryla.game.yetanotherengine.domain.game;

import static dbryla.game.yetanotherengine.domain.battleground.Position.ENEMIES_BACK;
import static dbryla.game.yetanotherengine.domain.battleground.Position.PLAYERS_BACK;
import static dbryla.game.yetanotherengine.domain.effects.Effect.CHARMED;
import static dbryla.game.yetanotherengine.domain.effects.Effect.FRIGHTENED;
import static dbryla.game.yetanotherengine.domain.effects.Effect.GRAPPLED;
import static dbryla.game.yetanotherengine.domain.effects.Effect.RESTRAINED;
import static dbryla.game.yetanotherengine.domain.subject.Affiliation.ENEMIES;
import static dbryla.game.yetanotherengine.domain.subject.Affiliation.PLAYERS;

import dbryla.game.yetanotherengine.domain.ai.ArtificialIntelligence;
import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.encounters.SpecialAttack;
import dbryla.game.yetanotherengine.domain.equipment.Weapon;
import dbryla.game.yetanotherengine.domain.events.Event;
import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.game.state.StateMachine;
import dbryla.game.yetanotherengine.domain.game.state.StateMachineFactory;
import dbryla.game.yetanotherengine.domain.game.state.storage.SubjectStorage;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.Affiliation;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Game {

  @Getter
  private final Long id;
  private final SubjectStorage subjectStorage;
  private final StateMachineFactory stateMachineFactory;
  private final ArtificialIntelligence artificialIntelligence;
  private final EventHub eventHub;

  private StateMachine stateMachine;

  public void createPlayerCharacter(Subject subject) {
    subjectStorage.save(id, subject);
  }

  public void createNonPlayableCharacters(List<Subject> subjects) {
    subjects.forEach(subject -> {
      subjectStorage.save(id, subject);
      artificialIntelligence.initSubject(this, subject);
    });
  }

  public List<String> getAllAliveAllyNames(Subject source) {
    return subjectStorage.findAll(id).stream()
        .filter(subject -> areAllies(source, subject) && subject.isAlive())
        .map(this::getSubjectName)
        .collect(Collectors.toUnmodifiableList());
  }

  private boolean areAllies(Subject source, Subject subject) {
    return source.getAffiliation().equals(subject.getAffiliation());
  }

  public List<Subject> getAllSubjects() {
    return subjectStorage.findAll(id);
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
    return subjectStorage.findAll(id).stream()
        .filter(subject -> ENEMIES.equals(subject.getAffiliation()) && subject.isAlive())
        .map(this::getSubjectName)
        .collect(Collectors.toUnmodifiableList());
  }

  private void gameLoop() {
    while (!stateMachine.isInTerminalState()) {
      if (stateMachine.getNextSubject().isPresent()) {
        Subject subject = stateMachine.getNextSubject().get();
        if (PLAYERS.equals(subject.getAffiliation())) {
          if (subject.cantMove()) {
            execute(new SubjectTurn(getSubjectName(subject)));
            return;
          }
          eventHub.notifySubjectAboutNextMove(id, subject);
          return;
        } else {
          stateMachine.execute(artificialIntelligence.action(getSubjectName(subject)));
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
    return stateMachine.getNextSubject().map(this::getSubjectName);
  }

  private String getSubjectName(Subject subject) {
    return subject.getName();
  }

  public Subject getSubject(String subjectName) {
    return subjectStorage.findByIdAndName(id, subjectName).orElse(null);
  }

  public void cleanup() {
    subjectStorage.deleteAll(id);
  }

  public boolean isEnded() {
    return stateMachine.isInTerminalState();
  }

  public int getPlayersNumber() {
    return (int) subjectStorage.findAll(id).stream().filter(subject -> PLAYERS.equals(subject.getAffiliation())).count();
  }

  public Map<Position, List<Subject>> getSubjectsPositionsMap() {
    return subjectStorage.findAll(id)
        .stream()
        .filter(subject -> !subject.isTerminated())
        .collect(Collectors.groupingBy(Subject::getPosition));
  }

  public void moveSubject(String playerName, Position newPosition) {
    subjectStorage.findByIdAndName(id, playerName).ifPresent(subject -> subjectStorage.save(id, subject.of(subject.newState(newPosition))));
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

  private List<String> getPossibleTargets(Subject source, int minRange, int maxRange, boolean allies) {
    Affiliation targetsAffiliation = allies ? source.getAffiliation() : getEnemyAffiliation(source.getAffiliation());
    int position = source.getPosition().getBattlegroundLocation();
    Map<Position, List<Subject>> positionsMap = getSubjectsPositionsMap();
    return getPositionsStream(source, minRange, maxRange, position)
        .mapToObj(battlegroundPosition -> positionsMap.getOrDefault(Position.valueOf(battlegroundPosition), List.of())
            .stream()
            .filter(target -> targetsAffiliation.equals(target.getAffiliation())
                && !target.isTerminated()
                && isNotCharmed(source, target))
            .map(this::getSubjectName))
        .flatMap(Function.identity())
        .collect(Collectors.toList());
  }

  private boolean isNotCharmed(Subject source, Subject target) {
    return source.getConditions()
        .stream()
        .filter(activeEffect -> CHARMED.equals(activeEffect.getEffect()))
        .noneMatch(condition -> target.getName().equals(condition.getSource()));
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
    return !getSubjectsPositionsMap()
        .getOrDefault(subject.getPosition(), List.of())
        .stream()
        .allMatch(anySubject -> areAllies(subject, anySubject));
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

  public boolean canMoveToPosition(Subject subject, int position) {
    if (position < 0
        || position > 4
        || subject.getConditions().stream().anyMatch(condition -> Set.of(GRAPPLED, RESTRAINED).contains(condition.getEffect()))) {
      return false;
    }
    boolean isMovingBack = !isAheadOfSubject(subject, Position.valueOf(position));
    return isMovingBack || !areEnemiesOnCurrentPosition(subject) && isNotFrightenedAnyOneAhead(subject);
  }

  public boolean isNotFrightenedAnyOneAhead(Subject subject) {
    Set<String> subjects = getSubjectsPositionsMap()
        .entrySet()
        .stream()
        .filter(entry -> isAheadOfSubject(subject, entry.getKey()))
        .flatMap(entry -> entry.getValue().stream())
        .map(this::getSubjectName)
        .collect(Collectors.toSet());

    return subject.getConditions()
        .stream()
        .filter(activeEffect -> FRIGHTENED.equals(activeEffect.getEffect()))
        .noneMatch(condition -> subjects.contains(condition.getSource()));
  }

  public boolean isAheadOfSubject(Subject subject, Position newPosition) {
    return newPosition.getBattlegroundLocation()
        * subject.getAffiliation().getDirection()
        > subject.getPosition().getBattlegroundLocation()
        * subject.getAffiliation().getDirection();
  }
}
