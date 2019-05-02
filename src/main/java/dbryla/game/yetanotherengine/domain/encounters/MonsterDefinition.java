package dbryla.game.yetanotherengine.domain.encounters;

import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.operations.DamageType;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.Abilities;
import dbryla.game.yetanotherengine.domain.subject.Race;
import dbryla.game.yetanotherengine.domain.subject.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import lombok.*;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Getter
@Builder
@ToString
class MonsterDefinition {
  private final String defaultName;
  private final String type;
  private final Race monsterRace;
  private final int numberOfHitDices;
  private final int hitDice;
  private final Abilities abilities;
  @Singular
  private final List<Weapon> weapons;
  private final Armor armor;
  private final Armor shield;
  private final double challengeRating;
  private final Position preferredPosition;
  private final List<Spell> spells;
  @Singular
  private final Set<SpecialAttack> specialAttacks;
  @Singular
  private final Set<DamageType> immunities;
}
