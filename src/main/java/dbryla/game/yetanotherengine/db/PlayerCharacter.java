package dbryla.game.yetanotherengine.db;

import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.Abilities;
import dbryla.game.yetanotherengine.domain.subject.Affiliation;
import dbryla.game.yetanotherengine.domain.subject.CharacterClass;
import dbryla.game.yetanotherengine.domain.subject.Race;
import dbryla.game.yetanotherengine.domain.equipment.Armor;
import dbryla.game.yetanotherengine.domain.equipment.Weapon;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PlayerCharacter {

  private String id;
  private String name;
  private Affiliation affiliation;
  private CharacterClass characterClass;
  private Race race;
  private int maxHealthPoints;
  private List<Weapon> weapons;
  private Armor shield;
  private Armor armor;
  private Abilities abilities;
  private List<Spell> spells;
}

