package dbryla.game.yetanotherengine.domain.game;

import dbryla.game.yetanotherengine.domain.TestData;
import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.encounters.MonstersFactory;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.CharacterClass;
import dbryla.game.yetanotherengine.domain.subject.Race;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.SubjectFactory;
import dbryla.game.yetanotherengine.domain.equipment.Weapon;
import dbryla.game.yetanotherengine.domain.subject.SubjectProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static dbryla.game.yetanotherengine.domain.battleground.Position.*;
import static dbryla.game.yetanotherengine.domain.subject.Affiliation.PLAYERS;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class GameITest {

  @Autowired
  private GameFactory gameFactory;

  @Autowired
  private SubjectFactory subjectFactory;

  @Autowired
  private MonstersFactory monstersFactory;

  private Game game;

  private static long gameNumber = 0;

  @BeforeEach
  void setUp() {
    game = gameFactory.newGame(gameNumber++);
  }

  @Test
  void shouldReturnListOfPossibleTargetsForMeleeWeapon() {
    String playerName = "player1";
    Weapon weapon = Weapon.SHORTSWORD;
    List<Subject> enemies = givenEnemies();
    Subject subject = initializedGame(playerName, List.of(weapon), enemies, ENEMIES_FRONT);

    List<String> possibleTargets = game.getPossibleTargets(subject, weapon);

    assertThat(possibleTargets).containsOnly(enemies.stream()
        .filter(enemy -> ENEMIES_FRONT.equals(enemy.getPosition()))
        .map(Subject::getName)
        .toArray(String[]::new));
  }

  private Subject initializedGame(String playerName, List<Weapon> weapons, List<Subject> enemies, Position playerPosition) {
    Subject subject = subjectFactory.createNewSubject(
        SubjectProperties
            .builder()
            .name(playerName)
            .affiliation(PLAYERS)
            .race(Race.HUMAN)
            .characterClass(CharacterClass.FIGHTER)
            .weapons(weapons)
            .abilities(TestData.ABILITIES)
            .build());
    subject = subject.of(subject.newState(playerPosition));
    game.createPlayerCharacter(subject);
    game.createNonPlayableCharacters(enemies);
    return subject;
  }

  private List<Subject> givenEnemies() {
    List<Subject> enemies = monstersFactory.createEncounter(1);
    enemies.addAll(monstersFactory.createEncounter(1));
    return enemies;
  }

  @Test
  void shouldReturnListOfPossibleTargetsForRangedWeaponMaxRange() {
    String playerName = "player1";
    Weapon weapon = Weapon.LONGBOW;
    List<Subject> enemies = givenEnemies();
    Subject subject = initializedGame(playerName, List.of(weapon), enemies, PLAYERS_BACK);

    List<String> possibleTargets = game.getPossibleTargets(subject, weapon);

    assertThat(possibleTargets).containsOnly(enemies.stream().map(Subject::getName).toArray(String[]::new));
  }

  @Test
  void shouldReturnListOfPossibleTargetsForRangedWeaponMinRange() {
    String playerName = "player1";
    Weapon weapon = Weapon.LONGBOW;
    List<Subject> enemies = givenEnemies();
    Subject subject = initializedGame(playerName, List.of(weapon), enemies, ENEMIES_FRONT);

    List<String> possibleTargets = game.getPossibleTargets(subject, weapon);

    assertThat(possibleTargets).containsOnly(enemies.stream()
        .filter(enemy -> ENEMIES_BACK.equals(enemy.getPosition()))
        .map(Subject::getName)
        .toArray(String[]::new));
  }

  @Test
  void shouldReturnListOfPossibleTargetsForSpellAttackMaxRange() {
    String playerName = "player1";
    Spell spell = Spell.FIRE_BOLT;
    List<Subject> enemies = givenEnemies();
    Subject subject = initializedGame(playerName, null, enemies, PLAYERS_BACK);

    List<String> possibleTargets = game.getPossibleTargets(subject, spell);

    assertThat(possibleTargets).containsOnly(enemies.stream().map(Subject::getName).toArray(String[]::new));
  }

  @Test
  void shouldReturnListOfPossibleTargetsForSpellAttackMinRange() {
    String playerName = "player1";
    Spell spell = Spell.FIRE_BOLT;
    List<Subject> enemies = givenEnemies();
    Subject subject = initializedGame(playerName, null, enemies, ENEMIES_FRONT);

    List<String> possibleTargets = game.getPossibleTargets(subject, spell);

    assertThat(possibleTargets).containsOnly(enemies.stream()
        .filter(enemy -> ENEMIES_BACK.equals(enemy.getPosition()))
        .map(Subject::getName)
        .toArray(String[]::new));
  }


  @Test
  void shouldReturnListOfPossibleTargetsForSpellWithCloseRange() {
    String playerName = "player1";
    Spell spell = Spell.BURNING_HANDS;
    List<Subject> enemies = givenEnemies();
    Subject subject = initializedGame(playerName, null, enemies, ENEMIES_FRONT);

    List<String> possibleTargets = game.getPossibleTargets(subject, spell);

    assertThat(possibleTargets).containsOnly(enemies.stream()
        .filter(enemy -> ENEMIES_FRONT.equals(enemy.getPosition()))
        .map(Subject::getName)
        .toArray(String[]::new));
  }

  @Test
  void shouldReturnEmptyListIfNoPossibleTargetsForSpellWithCloseRange() {
    String playerName = "player1";
    Spell spell = Spell.BURNING_HANDS;
    List<Subject> enemies = givenEnemies();
    Subject subject = initializedGame(playerName, null, enemies, PLAYERS_BACK);

    List<String> possibleTargets = game.getPossibleTargets(subject, spell);

    assertThat(possibleTargets).isEmpty();
  }

}