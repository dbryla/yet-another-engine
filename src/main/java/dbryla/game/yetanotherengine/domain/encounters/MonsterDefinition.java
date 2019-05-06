package dbryla.game.yetanotherengine.domain.encounters;

import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.effects.Effect;
import dbryla.game.yetanotherengine.domain.operations.DamageType;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.Abilities;
import dbryla.game.yetanotherengine.domain.subject.Race;
import dbryla.game.yetanotherengine.domain.subject.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import lombok.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class MonsterDefinition {
  private String defaultName;
  private String type;
  private Race monsterRace;
  private int numberOfHitDices;
  private int hitDice;
  private Abilities abilities;
  private List<Weapon> weapons;
  private Armor armor;
  private Armor shield;
  private double challengeRating;
  private Position preferredPosition;
  private List<Spell> spells = new LinkedList<>();
  private Set<SpecialAttack> specialAttacks = new HashSet<>();
  private Set<DamageType> immunities = new HashSet<>();
  private Set<Effect> advantageOnSavingThrowsAgainstEffects = new HashSet<>();
}
