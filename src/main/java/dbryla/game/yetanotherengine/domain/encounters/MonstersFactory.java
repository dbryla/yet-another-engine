package dbryla.game.yetanotherengine.domain.encounters;

import dbryla.game.yetanotherengine.domain.dice.DiceRollService;
import dbryla.game.yetanotherengine.domain.subject.Race;
import dbryla.game.yetanotherengine.domain.subject.State;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.SubjectProperties;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
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
        monsters.add(buildMonster(monsterDefinition, getMonsterName(monsterDefinition, monstersNumber), monsterRace));
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
    int monsterHealthPoints = getMonsterHealthPoints(monsterDefinition);
    SubjectProperties subjectProperties = SubjectProperties.builder()
        .name(monsterName)
        .affiliation(ENEMIES)
        .healthPoints(monsterHealthPoints)
        .abilities(monsterDefinition.getAbilities())
        .armor(monsterDefinition.getArmor())
        .weapons(monsterDefinition.getWeapons())
        .shield(monsterDefinition.getShield())
        .spells(monsterDefinition.getSpells())
        .race(race)
        .specialAttacks(monsterDefinition.getSpecialAttacks())
        .advantageOnSavingThrows(monsterDefinition.getAdvantageOnSavingThrowsAgainstEffects())
        .build();
    State state = new State(monsterName, monsterHealthPoints, monsterHealthPoints,
        monsterDefinition.getPreferredPosition(), Set.of(), monsterDefinition.getWeapons().get(0));
    return new Subject(subjectProperties, state);
  }

  private String getMonsterName(MonsterDefinition monsterDefinition, int monstersNumber) {
    if (monstersNumber == 1) {
      return monsterDefinition.getDefaultName();
    }
    return getRandomElement(monstersNames.getModifiers(monsterDefinition.getMonsterRace())) + " " + monsterDefinition.getDefaultName();
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
