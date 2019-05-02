package dbryla.game.yetanotherengine.domain.encounters;

import dbryla.game.yetanotherengine.domain.operations.ActionData;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

import static dbryla.game.yetanotherengine.domain.battleground.Distance.CLOSE_RANGE;
import static dbryla.game.yetanotherengine.domain.battleground.Distance.THIRTY_FEET;

@AllArgsConstructor
@Getter
public enum SpecialAttack {
  MULTI_ATTACK(CLOSE_RANGE, CLOSE_RANGE, false, List.of(new ActionData(Weapon.SCIMITAR))),
  POUNCE(THIRTY_FEET, THIRTY_FEET, true, List.of(new ActionData(Weapon.CLAW), new ActionData(Weapon.BITE)));

  private final int minRange;
  private final int maxRange;
  private final boolean withMove;
  private final List<ActionData> nestedActionData;

}
