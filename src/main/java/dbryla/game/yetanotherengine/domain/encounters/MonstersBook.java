package dbryla.game.yetanotherengine.domain.encounters;

import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.Abilities;
import dbryla.game.yetanotherengine.domain.subject.Race;
import dbryla.game.yetanotherengine.domain.subject.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.*;

import static dbryla.game.yetanotherengine.domain.operations.DamageType.POISON;

@Component
public class MonstersBook {

  @Getter
  private final List<MonsterDefinition> monsters = new LinkedList<>() {
    {
      add(MonsterDefinition.builder()
          .abilities(new Abilities(11, 12, 10, 10, 11, 10))
          .armor(Armor.LEATHER)
          .weapon(Weapon.SCIMITAR)
          .challengeRating(0.125)
          .defaultName("Cultist")
          .hitDice(8)
          .numberOfHitDices(2)
          .type("Cultist")
          .monsterRace(Race.HUMANOID)
          .preferredPosition(Position.ENEMIES_FRONT)
          .build());
      add(MonsterDefinition.builder()
          .abilities(new Abilities(13, 12, 12, 10, 11, 10))
          .armor(Armor.CHAIN_SHIRT)
          .shield(Armor.SHIELD)
          .weapons(List.of(Weapon.SHORTBOW, Weapon.SHORTSWORD)) // fixme spear
          .challengeRating(0.125)
          .defaultName("Guard")
          .hitDice(8)
          .numberOfHitDices(2)
          .type("Guard")
          .monsterRace(Race.HUMANOID)
          .preferredPosition(Position.ENEMIES_FRONT)
          .build());
      add(MonsterDefinition.builder()
          .abilities(new Abilities(10, 10, 10, 10, 14, 11))
          .challengeRating(0.25)
          .defaultName("Acolyte")
          .hitDice(8)
          .numberOfHitDices(2)
          .type("Acolyte")
          .spells(new ArrayList<>(List.of(Spell.SACRED_FLAME, Spell.BLESS, Spell.CURE_WOUNDS)))
          .weapon(Weapon.CLUB)
          .monsterRace(Race.HUMANOID)
          .preferredPosition(Position.ENEMIES_BACK)
          .build());
      add(MonsterDefinition.builder()
          .abilities(new Abilities(8, 14, 10, 10, 8, 8))
          .challengeRating(0.25)
          .defaultName("Goblin")
          .hitDice(6)
          .numberOfHitDices(2)
          .type("Goblin")
          .weapons(List.of(Weapon.SHORTBOW, Weapon.SCIMITAR))
          .armor(Armor.LEATHER)
          .shield(Armor.SHIELD)
          .monsterRace(Race.GOBLINOID)
          .preferredPosition(Position.ENEMIES_FRONT)
          .build());
      add(MonsterDefinition.builder()
          .abilities(new Abilities(10, 14, 10, 10, 8, 10))
          .challengeRating(1)
          .defaultName("Goblin Boss")
          .hitDice(6)
          .numberOfHitDices(6)
          .type("Goblin")
          .weapons(List.of(Weapon.SCIMITAR))
          .armor(Armor.CHAIN_SHIRT)
          .shield(Armor.SHIELD)
          .monsterRace(Race.GOBLINOID)
          .preferredPosition(Position.ENEMIES_FRONT)
          .specialAttack(SpecialAttack.MULTI_ATTACK)
          .build());
      add(MonsterDefinition.builder()
          .abilities(new Abilities(14, 15, 10, 3, 14, 7))
          .challengeRating(0.25)
          .defaultName("Panther")
          .hitDice(8)
          .numberOfHitDices(3)
          .type("Panther")
          .weapons(List.of(Weapon.BITE, Weapon.CLAW))
          .monsterRace(Race.BEAST)
          .preferredPosition(Position.ENEMIES_FRONT)
          .specialAttack(SpecialAttack.POUNCE)
          .build());
      add(MonsterDefinition.builder()
          .abilities(new Abilities(10, 14, 15, 6, 8, 5))
          .challengeRating(0.25)
          .defaultName("Skeleton")
          .hitDice(8)
          .numberOfHitDices(2)
          .type("Skeleton")
          .weapons(List.of(Weapon.SHORTBOW, Weapon.SHORTSWORD))
          .armor(Armor.SCRAPS)
          .monsterRace(Race.UNDEAD)
          .preferredPosition(Position.ENEMIES_FRONT)
          .immunity(POISON)
          .build());
    }
  };

  private final Random random;

  public MonstersBook(Random random) {
    this.random = random;
  }

  MonsterDefinition getRandomMonster(int playersLevel) {
    return monsters.get(random.nextInt(monsters.size()));
  }
}
