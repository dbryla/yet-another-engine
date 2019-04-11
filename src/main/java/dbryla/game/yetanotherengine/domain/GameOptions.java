package dbryla.game.yetanotherengine.domain;

import dbryla.game.yetanotherengine.domain.encounters.MonsterBook;
import dbryla.game.yetanotherengine.domain.encounters.MonsterDefinition;
import dbryla.game.yetanotherengine.domain.subjects.Monster;
import dbryla.game.yetanotherengine.domain.subjects.classes.Cleric;
import dbryla.game.yetanotherengine.domain.subjects.classes.Fighter;
import dbryla.game.yetanotherengine.domain.subjects.classes.Wizard;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Weapon;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GameOptions {

  public static final String ALLIES = "player";
  public static final String ENEMIES = "enemies";
  private static final Set<Class> AVAILABLE_CLASSES = Set.of(Fighter.class, Wizard.class, Cleric.class);
  private static final Set<String> SPELL_CASTERS = Set.of(Wizard.class.getSimpleName(), Cleric.class.getSimpleName());
  private final MonsterBook monsterBook;

  public Set<Class> getAvailableClasses() {
    return AVAILABLE_CLASSES;
  }

  public Set<Weapon> getAvailableWeapons(String className) {
    if (Fighter.class.getSimpleName().equals(className)) {
      return Arrays.stream(Weapon.values()).filter(Weapon::isPlayable).collect(Collectors.toSet());
    }
    if (Wizard.class.getSimpleName().equals(className)) {
      return Set.of(Weapon.DAGGER, Weapon.QUARTERSTAFF);
    }
    if (Cleric.class.getSimpleName().equals(className)) {
      return Arrays.stream(Weapon.values()).filter(Weapon::isSimpleType).collect(Collectors.toSet());
    }
    return Set.of();
  }

  public Set<Armor> getAvailableArmors(String className) {
    if (Fighter.class.getSimpleName().equals(className)) {
      return Arrays.stream(Armor.values()).filter(Armor::isPlayable).collect(Collectors.toSet());
    }
    if (Cleric.class.getSimpleName().equals(className)) {
      return Arrays.stream(Armor.values()).filter(Armor::isLightOrMedium).collect(Collectors.toSet());
    }
    return Set.of();
  }

  public boolean isSpellCaster(String className) {
    return SPELL_CASTERS.contains(className);
  }

  public List<Monster> getRandomEncounter(int playersNumber) {
    MonsterDefinition monsterDefinition = getMonster(playersNumber);
    int monstersNumber = getMonstersNumber(playersNumber, monsterDefinition.getChallengeRating());
    return createMonsters(monsterDefinition, monstersNumber);
  }

  private List<Monster> createMonsters(MonsterDefinition monsterDefinition, int monstersNumber) {
    List<Monster> monsters = new LinkedList<>();
    for (int i = 0; i < monstersNumber; ++i) {
      monsters.add(Monster.builder()
          .name(monstersNumber == 1 ? monsterDefinition.getDefaultName() : getMonsterName(monsterDefinition, i))
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
      monsterDefinition = monsterBook.getRandomMonster(1);
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
        .map(j -> DiceRoll.of(monsterDefinition.getHitDice()))
        .sum();
    return healthPoints + (monsterDefinition.getNumberOfHitDices() * monsterDefinition.getAbilities().getConstitutionModifier());
  }

  public List<Monster> getEncounter(int playersNumber, int encounterNumber) {
    MonsterDefinition monsterDefinition = monsterBook.getMonsters().get(encounterNumber);
    int monstersNumber = getMonstersNumber(playersNumber, monsterDefinition.getChallengeRating());
    return createMonsters(monsterDefinition, monstersNumber == 0 ? 1 : monstersNumber);
  }
}
