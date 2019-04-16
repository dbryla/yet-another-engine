package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import lombok.Getter;

@Getter
public class ActionData {

  private final Weapon weapon;
  private final Spell spell;
  private final Position position;

  public ActionData(Weapon weapon) {
    this.weapon = weapon;
    this.spell = null;
    this.position = null;
  }

  public ActionData(Spell spell) {
    this.spell = spell;
    this.weapon = null;
    this.position = null;
  }

  public ActionData(Position position) {
    this.position = position;
    this.spell = null;
    this.weapon = null;
  }
}
