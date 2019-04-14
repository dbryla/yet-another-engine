package dbryla.game.yetanotherengine.domain.encounters;

import dbryla.game.yetanotherengine.domain.dice.DiceRollService;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import static dbryla.game.yetanotherengine.domain.game.GameOptions.ENEMIES;

@AllArgsConstructor
@Component
public class MonstersFactory {

  private final MonstersBook monstersBook;
  private final DiceRollService diceRollService;

  public List<Subject> createEncounter(int playersNumber) {
    MonsterDefinition monsterDefinition = getMonster(playersNumber);
    int monstersNumber = getMonstersNumber(playersNumber, monsterDefinition.getChallengeRating());
    return createMonsters(monsterDefinition, monstersNumber);
  }

  public List<Subject> createEncounter(int playersNumber, int encounterNumber) {
    MonsterDefinition monsterDefinition = monstersBook.getMonsters().get(encounterNumber);
    int monstersNumber = getMonstersNumber(playersNumber, monsterDefinition.getChallengeRating());
    return createMonsters(monsterDefinition, monstersNumber == 0 ? 1 : monstersNumber);
  }

  private List<Subject> createMonsters(MonsterDefinition monsterDefinition, int monstersNumber) {
    List<Subject> monsters = new LinkedList<>();
    for (int i = 0; i < monstersNumber; ++i) {
      monsters.add(Subject.builder()
          .name(monstersNumber == 1 ? monsterDefinition.getDefaultName() : getMonsterName(monsterDefinition, i))
          .affiliation(ENEMIES)
          .healthPoints(getMonsterHealthPoints(monsterDefinition))
          .abilities(monsterDefinition.getAbilities())
          .armor(monsterDefinition.getArmor())
          .weapon(monsterDefinition.getWeapon())
          .shield(monsterDefinition.getShield())
          .spells(monsterDefinition.getSpells())
          .build());
    }
    return monsters;
  }

  private String getMonsterName(MonsterDefinition monsterDefinition, int i) {
    return monsterDefinition.getAdjectives().get(i) + " " + monsterDefinition.getDefaultName();
  }

  private MonsterDefinition getMonster(int playersNumber) {
    int monstersNumber = 0;
    MonsterDefinition monsterDefinition = null;
    while (monstersNumber == 0) {
      monsterDefinition = monstersBook.getRandomMonster(1);
      monstersNumber = getMonstersNumber(playersNumber, monsterDefinition.getChallengeRating());
    }
    return monsterDefinition;
  }

  private int getMonstersNumber(int playersNumber, double challengeRating) {
    if (challengeRating == 0.125) {
      return playersNumber;
    }
    if (challengeRating == 0.25) {
      return playersNumber / 2;
    }
    if (challengeRating == 0.5) {
      return playersNumber / 4;
    }
    return 0;
  }

  private int getMonsterHealthPoints(MonsterDefinition monsterDefinition) {
    int healthPoints = IntStream.range(0, monsterDefinition.getNumberOfHitDices())
        .map(j -> diceRollService.of(monsterDefinition.getHitDice()))
        .sum();
    return healthPoints + (monsterDefinition.getNumberOfHitDices() * monsterDefinition.getAbilities().getConstitutionModifier());
  }
}
