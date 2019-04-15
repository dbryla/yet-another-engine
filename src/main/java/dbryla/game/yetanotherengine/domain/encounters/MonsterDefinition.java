package dbryla.game.yetanotherengine.domain.encounters;

import dbryla.game.yetanotherengine.domain.subject.Abilities;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.Race;
import dbryla.game.yetanotherengine.domain.subject.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class MonsterDefinition {
  private final String defaultName;
  private final String type;
  private final Race monsterRace;
  private final int numberOfHitDices;
  private final int hitDice;
  private final Abilities abilities;
  private final Weapon weapon;
  private final Armor armor;
  private final Armor shield;
  private final double challengeRating;
  private final List<Spell> spells;
}
