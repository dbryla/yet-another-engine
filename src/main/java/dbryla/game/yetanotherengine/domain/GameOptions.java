package dbryla.game.yetanotherengine.domain;

import dbryla.game.yetanotherengine.domain.encounters.MonsterBook;
import dbryla.game.yetanotherengine.domain.encounters.MonsterDefinition;
import dbryla.game.yetanotherengine.domain.state.SubjectIdentifier;
import dbryla.game.yetanotherengine.domain.subjects.Monster;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.domain.subjects.classes.*;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Weapon;

import java.util.*;
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
  private static final Set<Class> SPELL_CASTERS = Set.of(Wizard.class, Cleric.class);
  private final MonsterBook monsterBook;

  public Set<Class> getAvailableClasses() {
    return AVAILABLE_CLASSES;
  }

  public Set<Weapon> getAvailableWeapons(Class clazz) {
    if (Fighter.class.equals(clazz)) {
      return Set.of(Weapon.values());
    }
    if (Wizard.class.equals(clazz)) {
      return Set.of(Weapon.DAGGER, Weapon.QUARTERSTAFF);
    }
    if (Cleric.class.equals(clazz)) {
      return Arrays.stream(Weapon.values()).filter(Weapon::isSimpleType).collect(Collectors.toSet());
    }
    return Set.of();
  }

  public Set<Armor> getAvailableArmors(Class clazz) {
    if (Fighter.class.equals(clazz)) {
      Set<Armor> armors = new HashSet<>(Set.of(Armor.values()));
      armors.remove(Armor.SHIELD);
      return armors;
    }
    if (Cleric.class.equals(clazz)) {
      return Arrays.stream(Armor.values()).filter(Armor::isNotHeavyArmor).collect(Collectors.toSet());
    }
    return Set.of();
  }

  public boolean isSpellCaster(Class clazz) {
    return SPELL_CASTERS.contains(clazz);
  }

  List<Subject> getRandomEncounter(int playersNumber) {
    MonsterDefinition monsterDefinition = monsterBook.getRandomMonster();
    List<Subject> monsters = new LinkedList<>();
    for (int i = 0; i < playersNumber; ++i) {
      int healthPoints = IntStream.range(0, monsterDefinition.getNumberOfHitDices())
          .map(j -> DiceRoll.of(monsterDefinition.getHitDice()))
          .sum();
      monsters.add(Monster.builder()
          .name(monsterDefinition.getDefaultName() + i)
          .healthPoints(healthPoints)
          .abilities(monsterDefinition.getAbilities())
          .armor(monsterDefinition.getArmor())
          .weapon(monsterDefinition.getWeapon())
          .shield(monsterDefinition.getShield())
          .build());
    }
    return monsters;
  }

}
