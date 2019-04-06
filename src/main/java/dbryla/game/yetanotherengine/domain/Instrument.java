package dbryla.game.yetanotherengine.domain;

import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Weapon;
import lombok.Getter;

@Getter
public class Instrument {

  private final Weapon weapon;
  private final Spell spell;

  public Instrument(Weapon weapon) {
    this.weapon = weapon;
    this.spell = null;
  }

  public Instrument(Spell spell) {
    this.spell = spell;
    this.weapon = null;
  }
}
