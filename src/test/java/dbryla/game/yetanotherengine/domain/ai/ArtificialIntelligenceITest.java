package dbryla.game.yetanotherengine.domain.ai;

import dbryla.game.yetanotherengine.domain.TestData;
import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.game.GameFactory;
import dbryla.game.yetanotherengine.domain.game.SubjectTurn;
import dbryla.game.yetanotherengine.domain.operations.OperationType;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.Affiliation;
import dbryla.game.yetanotherengine.domain.subject.Race;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Random;

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
    Subject testedSubject = Subject.builder()
        .name(testSubjectName)
        .race(Race.GOBLINOID)
        .affiliation(Affiliation.PLAYERS)
        .abilities(TestData.ABILITIES)
        .weapons(List.of(Weapon.SHORTSWORD, Weapon.SHORTBOW))
        .position(Position.PLAYERS_BACK)
        .healthPoints(10)
        .build();
    String targetName = "target";
    Subject target = Subject.builder()
        .name(targetName)
        .race(Race.HALF_ORC)
        .affiliation(Affiliation.ENEMIES)
        .abilities(TestData.ABILITIES)
        .weapons(List.of(Weapon.SHORTSWORD))
        .position(Position.PLAYERS_BACK)
        .healthPoints(10)
        .build();
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
    Subject testedSubject = Subject.builder()
        .name(testSubjectName)
        .race(Race.GOBLINOID)
        .affiliation(Affiliation.PLAYERS)
        .abilities(TestData.ABILITIES)
        .weapons(List.of(Weapon.SHORTSWORD, Weapon.SHORTBOW))
        .equippedWeapon(Weapon.SHORTSWORD)
        .position(Position.PLAYERS_BACK)
        .healthPoints(10)
        .build();
    String targetName = "target";
    Subject target = Subject.builder()
        .name(targetName)
        .race(Race.HALF_ORC)
        .affiliation(Affiliation.ENEMIES)
        .abilities(TestData.ABILITIES)
        .weapons(List.of(Weapon.SHORTSWORD))
        .position(Position.PLAYERS_BACK)
        .healthPoints(10)
        .build();
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
    Subject testedSubject = Subject.builder()
        .name(testSubjectName)
        .race(Race.GOBLINOID)
        .affiliation(Affiliation.PLAYERS)
        .abilities(TestData.ABILITIES)
        .weapons(List.of(Weapon.SHORTSWORD, Weapon.SHORTBOW))
        .position(Position.PLAYERS_BACK)
        .healthPoints(10)
        .build();
    String targetName = "target";
    Subject target = Subject.builder()
        .name(targetName)
        .race(Race.HALF_ORC)
        .affiliation(Affiliation.ENEMIES)
        .abilities(TestData.ABILITIES)
        .weapons(List.of(Weapon.SHORTSWORD))
        .position(Position.ENEMIES_FRONT)
        .healthPoints(10)
        .build();
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
    Subject testedSubject = Subject.builder()
        .name(testSubjectName)
        .race(Race.GOBLINOID)
        .affiliation(Affiliation.PLAYERS)
        .abilities(TestData.ABILITIES)
        .weapons(List.of(Weapon.SHORTSWORD))
        .equippedWeapon(Weapon.SHORTSWORD)
        .position(Position.PLAYERS_BACK)
        .healthPoints(10)
        .build();
    String targetName = "target";
    Subject target = Subject.builder()
        .name(targetName)
        .race(Race.HALF_ORC)
        .affiliation(Affiliation.ENEMIES)
        .abilities(TestData.ABILITIES)
        .weapons(List.of(Weapon.SHORTSWORD))
        .position(Position.PLAYERS_FRONT)
        .healthPoints(10)
        .build();
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
    Subject testedSubject = Subject.builder()
        .name(testSubjectName)
        .race(Race.GOBLINOID)
        .affiliation(Affiliation.PLAYERS)
        .abilities(TestData.ABILITIES)
        .weapons(List.of(Weapon.SHORTSWORD))
        .equippedWeapon(Weapon.SHORTSWORD)
        .position(Position.PLAYERS_BACK)
        .healthPoints(10)
        .build();
    String targetName = "target";
    Subject target = Subject.builder()
        .name(targetName)
        .race(Race.HALF_ORC)
        .affiliation(Affiliation.ENEMIES)
        .abilities(TestData.ABILITIES)
        .weapons(List.of(Weapon.SHORTSWORD))
        .position(Position.MID)
        .healthPoints(10)
        .build();
    game.createNonPlayableCharacters(List.of(testedSubject, target));

    SubjectTurn turn = artificialIntelligence.action(testSubjectName);

    assertThat(turn.getActions()).isNotEmpty();
    assertThat(turn.getActions())
        .extracting("operationType", "actionData.position")
        .contains(tuple(OperationType.MOVE, Position.MID));
  }

  @Test
  void shouldCastSpellIfTargetInRange() {
    String testSubjectName = "tested-subject";
    Subject testedSubject = Subject.builder()
        .name(testSubjectName)
        .race(Race.GOBLINOID)
        .affiliation(Affiliation.PLAYERS)
        .abilities(TestData.ABILITIES)
        .weapons(List.of(Weapon.SHORTSWORD))
        .equippedWeapon(Weapon.SHORTSWORD)
        .position(Position.PLAYERS_BACK)
        .spells(List.of(Spell.SACRED_FLAME))
        .healthPoints(10)
        .build();
    String targetName = "target";
    Subject target = Subject.builder()
        .name(targetName)
        .race(Race.HALF_ORC)
        .affiliation(Affiliation.ENEMIES)
        .abilities(TestData.ABILITIES)
        .weapons(List.of(Weapon.SHORTSWORD))
        .position(Position.MID)
        .healthPoints(10)
        .build();
    game.createNonPlayableCharacters(List.of(testedSubject, target));

    SubjectTurn turn = artificialIntelligence.action(testSubjectName);

    assertThat(turn.getActions()).isNotEmpty();
    assertThat(turn.getActions())
        .extracting("operationType", "targetNames", "actionData.spell")
        .contains(tuple(OperationType.SPELL_CAST, List.of(targetName), Spell.SACRED_FLAME));
  }
}