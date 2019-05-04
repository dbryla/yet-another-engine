package dbryla.game.yetanotherengine.domain.encounters;

import dbryla.game.yetanotherengine.domain.dice.DiceRollService;
import dbryla.game.yetanotherengine.domain.subject.Race;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static dbryla.game.yetanotherengine.domain.subject.Affiliation.ENEMIES;
import static dbryla.game.yetanotherengine.domain.subject.Race.HUMANOID;

@AllArgsConstructor
@Component
public class MonstersFactory {

  private static final int DIFFICULTY_FACTOR = 1;
  private final MonstersBook monstersBook;
  private final DiceRollService diceRollService;
  private final Random random;
  private final MonstersNames monstersNames;

  public List<Subject> createEncounter(int playersNumber) {
    MonsterDefinition monsterDefinition = monstersBook.getRandomMonster(1);
    int monstersNumber = getMonstersNumber(playersNumber, monsterDefinition.getChallengeRating());
    return createMonsters(monsterDefinition, monstersNumber);
  }

  private List<Subject> createMonsters(MonsterDefinition monsterDefinition, int monstersNumber) {
    List<Subject> monsters = new LinkedList<>();
    Race monsterRace = monsterDefinition.getMonsterRace();
    if (HUMANOID.equals(monsterRace)) {
      for (int i = 0; i < monstersNumber; ++i) {
        monsters.add(buildHumanoidMonster(monsterDefinition, monstersNumber));
      }
    } else {
      for (int i = 0; i < monstersNumber; ++i) {
        monsters.add(buildMonster(monsterDefinition, getMonsterName(monsterDefinition, i, monstersNumber), monsterRace));
      }
    }
    return monsters;
  }

  private Subject buildHumanoidMonster(MonsterDefinition monsterDefinition, int monstersNumber) {
    List<Race> possibleRaces = Arrays.stream(Race.values()).filter(Race::isPlayable).collect(Collectors.toList());
    Race race = getRandomElement(possibleRaces);
    if (monstersNumber == 1) {
      return buildMonster(monsterDefinition, monsterDefinition.getDefaultName(), race);
    }
    return buildMonster(monsterDefinition, getHumanoidName(monsterDefinition, race), race);
  }

  private String getHumanoidName(MonsterDefinition monsterDefinition, Race race) {
    return getRandomElement(monstersNames.getModifiers(HUMANOID)) + " " + race.getDisplayName() + " " + monsterDefinition.getDefaultName();
  }

  private <T> T getRandomElement(List<T> elements) {
    return elements.get(random.nextInt(elements.size()));
  }

  private Subject buildMonster(MonsterDefinition monsterDefinition, String monsterName, Race race) {
    return Subject.builder()
        .name(monsterName)
        .affiliation(ENEMIES)
        .healthPoints(getMonsterHealthPoints(monsterDefinition))
        .abilities(monsterDefinition.getAbilities())
        .armor(monsterDefinition.getArmor())
        .weapons(monsterDefinition.getWeapons())
        .equippedWeapon(monsterDefinition.getWeapons().get(0))
        .shield(monsterDefinition.getShield())
        .spells(monsterDefinition.getSpells())
        .position(monsterDefinition.getPreferredPosition())
        .race(race)
        .specialAttacks(monsterDefinition.getSpecialAttacks())
        .build();
  }

  private String getMonsterName(MonsterDefinition monsterDefinition, int i, int monstersNumber) {
    if (monstersNumber == 1) {
      return monsterDefinition.getDefaultName();
    }
    return monstersNames.getModifiers(monsterDefinition.getMonsterRace()).get(i) + " " + monsterDefinition.getDefaultName();
  }

  private int getMonstersNumber(int playersNumber, double challengeRating) {
    if (challengeRating == 0.125) {
      return DIFFICULTY_FACTOR * playersNumber;
    }
    if (challengeRating == 0.25) {
      return Math.max(DIFFICULTY_FACTOR * playersNumber / 2, 1);
    }
    if (challengeRating == 0.5) {
      return Math.max(DIFFICULTY_FACTOR * playersNumber / 4, 1);
    }
    return 1;
  }

  private int getMonsterHealthPoints(MonsterDefinition monsterDefinition) {
    int healthPoints = IntStream.range(0, monsterDefinition.getNumberOfHitDices())
        .map(j -> diceRollService.of(monsterDefinition.getHitDice()))
        .sum();
    return healthPoints + (monsterDefinition.getNumberOfHitDices() * monsterDefinition.getAbilities().getConstitutionModifier());
  }
}
