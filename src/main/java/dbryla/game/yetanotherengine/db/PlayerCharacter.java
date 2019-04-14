package dbryla.game.yetanotherengine.db;

import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.Abilities;
import dbryla.game.yetanotherengine.domain.subject.ActiveEffect;
import dbryla.game.yetanotherengine.domain.subject.CharacterClass;
import dbryla.game.yetanotherengine.domain.subject.Race;
import dbryla.game.yetanotherengine.domain.subject.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
public class PlayerCharacter {

  private String id;
  private String name;
  private String affiliation;
  private CharacterClass characterClass;
  private Race race;
  private int maxHealthPoints;
  private Weapon weapon;
  private Armor shield;
  private Armor armor;
  private Abilities abilities;
  private List<Spell> spells;
}

