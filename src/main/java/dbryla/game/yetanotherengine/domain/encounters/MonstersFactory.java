package dbryla.game.yetanotherengine.domain.encounters;

import static dbryla.game.yetanotherengine.domain.game.GameOptions.ENEMIES;
import static dbryla.game.yetanotherengine.domain.subject.Race.HUMANOID;

import dbryla.game.yetanotherengine.domain.dice.DiceRollService;
import dbryla.game.yetanotherengine.domain.subject.Race;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
@Slf4j
public class MonstersFactory {

  private static final int DIFFICULTY_FACTOR = 1;
  private final MonstersBook monstersBook;
  private final DiceRollService diceRollService;
  private final Random random;
  private final MonstersNames monstersNames;

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
        .weapon(monsterDefinition.getWeapon())
        .shield(monsterDefinition.getShield())
        .spells(monsterDefinition.getSpells())
        .position(monsterDefinition.getPreferredPosition())
        .race(race)
        .build();
  }

  private String getMonsterName(MonsterDefinition monsterDefinition, int i, int monstersNumber) {
    if (monstersNumber == 1) {
      return monsterDefinition.getDefaultName();
    }
    return monstersNames.getModifiers(monsterDefinition.getMonsterRace()).get(i) + " " + monsterDefinition.getDefaultName();
  }

  private MonsterDefinition getMonster(int playersNumber) {
    int monstersNumber = 0;
    MonsterDefinition monsterDefinition = null;
    while (monstersNumber == 0) {
      monsterDefinition = monstersBook.getRandomMonster(1);
      monstersNumber = getMonstersNumber(playersNumber, monsterDefinition.getChallengeRating());
      log.trace("{} - Suggesting {} monsters.", monsterDefinition, monstersNumber);
    }
    return monsterDefinition;
  }

  private int getMonstersNumber(int playersNumber, double challengeRating) {
    if (challengeRating == 0.125) {
      return DIFFICULTY_FACTOR * playersNumber;
    }
    if (challengeRating == 0.25) {
      return DIFFICULTY_FACTOR * playersNumber / 2;
    }
    if (challengeRating == 0.5) {
      return DIFFICULTY_FACTOR * playersNumber / 4;
    }
    if (challengeRating == 1) {
      return DIFFICULTY_FACTOR * playersNumber / 4;
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
