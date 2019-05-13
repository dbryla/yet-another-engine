package dbryla.game.yetanotherengine.domain.ai;

import dbryla.game.yetanotherengine.domain.TestData;
import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.game.GameFactory;
import dbryla.game.yetanotherengine.domain.game.SubjectTurn;
import dbryla.game.yetanotherengine.domain.operations.OperationType;
import dbryla.game.yetanotherengine.domain.encounters.SpecialAttack;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.Affiliation;
import dbryla.game.yetanotherengine.domain.subject.Race;
import dbryla.game.yetanotherengine.domain.subject.State;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.equipment.Weapon;
import dbryla.game.yetanotherengine.domain.subject.SubjectFactory;
import dbryla.game.yetanotherengine.domain.subject.SubjectProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
class ArtificialIntelligenceITest {

  @Autowired
  private ArtificialIntelligence artificialIntelligence;

  @Autowired
  private GameFactory gameFactory;

  private Game game;

  private Random random = new Random();

  @BeforeEach
  void setUp() {
    game = gameFactory.newGame(random.nextLong());
  }

  @Test
  void shouldAttackCharacterWithinRange() {
    String testSubjectName = "tested-subject";
    SubjectProperties testSubjectProperties = SubjectProperties.builder()
        .name(testSubjectName)
        .race(Race.GOBLINOID)
        .affiliation(Affiliation.PLAYERS)
        .abilities(TestData.ABILITIES)
        .weapons(List.of(Weapon.SHORTSWORD, Weapon.SHORTBOW))
        .healthPoints(10)
        .build();
    Subject testedSubject =
        new Subject(testSubjectProperties,
            new State(testSubjectName, testSubjectProperties.getMaxHealthPoints(),
                testSubjectProperties.getMaxHealthPoints(), Position.PLAYERS_BACK, Set.of(), Weapon.SHORTSWORD));
    String targetName = "target";
    SubjectProperties targetProperties = SubjectProperties.builder()
        .name(targetName)
        .race(Race.HALF_ORC)
        .affiliation(Affiliation.ENEMIES)
        .abilities(TestData.ABILITIES)
        .weapons(List.of(Weapon.SHORTSWORD))
        .healthPoints(10)
        .build();
    Subject target =
        new Subject(targetProperties,
            new State(targetName, targetProperties.getMaxHealthPoints(),
                targetProperties.getMaxHealthPoints(), Position.PLAYERS_BACK, Set.of(), Weapon.SHORTSWORD));
    game.createNonPlayableCharacters(List.of(testedSubject, target));

    SubjectTurn turn = artificialIntelligence.action(testSubjectName);

    assertThat(turn.getActions()).isNotEmpty();
    assertThat(turn.getActions())
        .extracting("operationType", "targetNames", "actionData.weapon")
        .contains(tuple(OperationType.ATTACK, List.of(targetName), Weapon.SHORTSWORD));
  }

  @Test
  void shouldFollowAcquiredTargetIfInMoveAndAttackRange() {
    String testSubjectName = "tested-subject";
    SubjectProperties testSubjectProperties = SubjectProperties.builder()
        .name(testSubjectName)
        .race(Race.GOBLINOID)
        .affiliation(Affiliation.PLAYERS)
        .abilities(TestData.ABILITIES)
        .weapons(List.of(Weapon.SHORTSWORD, Weapon.SHORTBOW))
        .healthPoints(10)
        .build();
    Subject testedSubject =
        new Subject(testSubjectProperties,
            new State(testSubjectName, testSubjectProperties.getMaxHealthPoints(),
                testSubjectProperties.getMaxHealthPoints(), Position.PLAYERS_BACK, Set.of(), Weapon.SHORTSWORD));
    String targetName = "target";
    SubjectProperties targetProperties = SubjectProperties.builder()
        .name(targetName)
        .race(Race.HALF_ORC)
        .affiliation(Affiliation.ENEMIES)
        .abilities(TestData.ABILITIES)
        .weapons(List.of(Weapon.SHORTSWORD))
        .healthPoints(10)
        .build();
    Subject target =
        new Subject(targetProperties,
            new State(targetName, targetProperties.getMaxHealthPoints(),
                targetProperties.getMaxHealthPoints(), Position.PLAYERS_BACK, Set.of(), Weapon.SHORTSWORD));
    game.createNonPlayableCharacters(List.of(testedSubject, target));
    artificialIntelligence.action(testSubjectName);
    game.moveSubject(targetName, Position.PLAYERS_FRONT);

    SubjectTurn turn = artificialIntelligence.action(testSubjectName);

    assertThat(turn.getActions()).isNotEmpty();
    assertThat(turn.getActions())
        .extracting("operationType", "targetNames", "actionData.weapon")
        .contains(tuple(OperationType.ATTACK, List.of(targetName), Weapon.SHORTSWORD));
  }

  @Test
  void shouldUseDifferentThanEquippedWeaponIfCanAttackTargetWithIt() {
    String testSubjectName = "tested-subject";
    SubjectProperties testSubjectProperties = SubjectProperties.builder()
        .name(testSubjectName)
        .race(Race.GOBLINOID)
        .affiliation(Affiliation.PLAYERS)
        .abilities(TestData.ABILITIES)
        .weapons(List.of(Weapon.SHORTSWORD, Weapon.SHORTBOW))
        .healthPoints(10)
        .build();
    Subject testedSubject =
        new Subject(testSubjectProperties,
            new State(testSubjectName, testSubjectProperties.getMaxHealthPoints(),
                testSubjectProperties.getMaxHealthPoints(), Position.PLAYERS_BACK, Set.of(), Weapon.SHORTSWORD));
    String targetName = "target";
    SubjectProperties targetProperties = SubjectProperties.builder()
        .name(targetName)
        .race(Race.HALF_ORC)
        .affiliation(Affiliation.ENEMIES)
        .abilities(TestData.ABILITIES)
        .weapons(List.of(Weapon.SHORTSWORD))
        .healthPoints(10)
        .build();
    Subject target =
        new Subject(targetProperties,
            new State(targetName, targetProperties.getMaxHealthPoints(),
                targetProperties.getMaxHealthPoints(), Position.ENEMIES_FRONT, Set.of(), Weapon.SHORTSWORD));
    game.createNonPlayableCharacters(List.of(testedSubject, target));

    SubjectTurn turn = artificialIntelligence.action(testSubjectName);

    assertThat(turn.getActions()).isNotEmpty();
    assertThat(turn.getActions())
        .extracting("operationType", "targetNames", "actionData.weapon")
        .contains(tuple(OperationType.ATTACK, List.of(targetName), Weapon.SHORTBOW));
  }

  @Test
  void shouldMoveAndAttackTargetIfInMoveAndAttackRange() {
    String testSubjectName = "tested-subject";
    SubjectProperties testSubjectProperties = SubjectProperties.builder()
        .name(testSubjectName)
        .race(Race.GOBLINOID)
        .affiliation(Affiliation.PLAYERS)
        .abilities(TestData.ABILITIES)
        .weapons(List.of(Weapon.SHORTSWORD))
        .healthPoints(10)
        .build();
    Subject testedSubject =
        new Subject(testSubjectProperties,
            new State(testSubjectName, testSubjectProperties.getMaxHealthPoints(),
                testSubjectProperties.getMaxHealthPoints(), Position.PLAYERS_BACK, Set.of(), Weapon.SHORTSWORD));
    String targetName = "target";
    SubjectProperties targetProperties = SubjectProperties.builder()
        .name(targetName)
        .race(Race.HALF_ORC)
        .affiliation(Affiliation.ENEMIES)
        .abilities(TestData.ABILITIES)
        .weapons(List.of(Weapon.SHORTSWORD))
        .healthPoints(10)
        .build();
    Subject target =
        new Subject(targetProperties,
            new State(targetName, targetProperties.getMaxHealthPoints(),
                targetProperties.getMaxHealthPoints(), Position.PLAYERS_FRONT, Set.of(), Weapon.SHORTSWORD));
    game.createNonPlayableCharacters(List.of(testedSubject, target));

    SubjectTurn turn = artificialIntelligence.action(testSubjectName);

    assertThat(turn.getActions()).isNotEmpty();
    assertThat(turn.getActions())
        .extracting("operationType", "targetNames", "actionData.weapon")
        .contains(tuple(OperationType.ATTACK, List.of(targetName), Weapon.SHORTSWORD));
  }

  @Test
  void shouldMoveForwardIfNoTargetInMoveAndAttackRange() {
    String testSubjectName = "tested-subject";
    SubjectProperties testSubjectProperties = SubjectProperties.builder()
        .name(testSubjectName)
        .race(Race.GOBLINOID)
        .affiliation(Affiliation.PLAYERS)
        .abilities(TestData.ABILITIES)
        .weapons(List.of(Weapon.SHORTSWORD))
        .healthPoints(10)
        .build();
    Subject testedSubject =
        new Subject(testSubjectProperties,
            new State(testSubjectName, testSubjectProperties.getMaxHealthPoints(),
                testSubjectProperties.getMaxHealthPoints(), Position.PLAYERS_BACK, Set.of(), Weapon.SHORTSWORD));
    String targetName = "target";
    SubjectProperties targetProperties = SubjectProperties.builder()
        .name(targetName)
        .race(Race.HALF_ORC)
        .affiliation(Affiliation.ENEMIES)
        .abilities(TestData.ABILITIES)
        .weapons(List.of(Weapon.SHORTSWORD))
        .healthPoints(10)
        .build();
    Subject target =
        new Subject(targetProperties,
            new State(targetName, targetProperties.getMaxHealthPoints(),
                targetProperties.getMaxHealthPoints(), Position.MID, Set.of(), Weapon.SHORTSWORD));
    game.createNonPlayableCharacters(List.of(testedSubject, target));

    SubjectTurn turn = artificialIntelligence.action(testSubjectName);

    assertThat(turn.getActions()).isNotEmpty();
    assertThat(turn.getActions())
        .extracting("operationType", "actionData.position")
        .contains(tuple(OperationType.MOVE, Position.MID));
  }

  @Test
  void shouldCastSpellIfTargetInRange() {String testSubjectName = "tested-subject";
    SubjectProperties testSubjectProperties = SubjectProperties.builder()
        .name(testSubjectName)
        .race(Race.GOBLINOID)
        .affiliation(Affiliation.PLAYERS)
        .abilities(TestData.ABILITIES)
        .weapons(List.of(Weapon.SHORTSWORD))
        .spells(List.of(Spell.SACRED_FLAME))
        .healthPoints(10)
        .build();
    Subject testedSubject =
        new Subject(testSubjectProperties,
            new State(testSubjectName, testSubjectProperties.getMaxHealthPoints(),
                testSubjectProperties.getMaxHealthPoints(), Position.PLAYERS_BACK, Set.of(), Weapon.SHORTSWORD));
    String targetName = "target";
    SubjectProperties targetProperties = SubjectProperties.builder()
        .name(targetName)
        .race(Race.HALF_ORC)
        .affiliation(Affiliation.ENEMIES)
        .abilities(TestData.ABILITIES)
        .weapons(List.of(Weapon.SHORTSWORD))
        .healthPoints(10)
        .build();
    Subject target =
        new Subject(targetProperties,
            new State(targetName, targetProperties.getMaxHealthPoints(),
                targetProperties.getMaxHealthPoints(), Position.MID, Set.of(), Weapon.SHORTSWORD));

    game.createNonPlayableCharacters(List.of(testedSubject, target));

    SubjectTurn turn = artificialIntelligence.action(testSubjectName);

    assertThat(turn.getActions()).isNotEmpty();
    assertThat(turn.getActions())
        .extracting("operationType", "targetNames", "actionData.spell")
        .contains(tuple(OperationType.SPELL_CAST, List.of(targetName), Spell.SACRED_FLAME));
  }

  @Test
  void shouldReturnTurnWithSpecialAttack() {
    String testSubjectName = "tested-subject";
    SubjectProperties testSubjectProperties = SubjectProperties.builder()
        .name(testSubjectName)
        .race(Race.GOBLINOID)
        .affiliation(Affiliation.PLAYERS)
        .abilities(TestData.ABILITIES)
        .weapons(List.of(Weapon.SHORTSWORD, Weapon.SHORTBOW))
        .specialAttacks(Set.of(SpecialAttack.MULTI_ATTACK))
        .healthPoints(10)
        .build();
    Subject testedSubject =
        new Subject(testSubjectProperties,
            new State(testSubjectName, testSubjectProperties.getMaxHealthPoints(),
                testSubjectProperties.getMaxHealthPoints(), Position.PLAYERS_BACK, Set.of(), Weapon.SHORTSWORD));
    String targetName = "target";
    SubjectProperties targetProperties = SubjectProperties.builder()
        .name(targetName)
        .race(Race.HALF_ORC)
        .affiliation(Affiliation.ENEMIES)
        .abilities(TestData.ABILITIES)
        .weapons(List.of(Weapon.SHORTSWORD))
        .healthPoints(10)
        .build();
    Subject target =
        new Subject(targetProperties,
            new State(targetName, targetProperties.getMaxHealthPoints(),
                targetProperties.getMaxHealthPoints(), Position.PLAYERS_BACK, Set.of(), Weapon.SHORTSWORD));
    game.createNonPlayableCharacters(List.of(testedSubject, target));

    SubjectTurn turn = artificialIntelligence.action(testSubjectName);

    assertThat(turn.getActions()).isNotEmpty();
    assertThat(turn.getActions())
        .extracting("operationType", "targetNames", "actionData.specialAttack")
        .contains(tuple(OperationType.SPECIAL_ATTACK, List.of(targetName), SpecialAttack.MULTI_ATTACK));
  }
}