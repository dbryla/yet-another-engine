package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.IncorrectStateException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import static dbryla.game.yetanotherengine.domain.operations.OperationType.*;

@Component
@AllArgsConstructor
public class OperationFactory {

  private final AttackOperation attackOperation;
  private final SpellCastOperation spellCastOperation;
  private final MoveOperation moveOperation;
  private final SpecialAttackOperation specialAttackOperation;
  private final StandUpOperation standUpOperation;

  public Operation getOperation(OperationType operationType) {
    if (ATTACK.equals(operationType)) {
      return attackOperation;
    }
    if (SPELL_CAST.equals(operationType)) {
      return spellCastOperation;
    }
    if (MOVE.equals(operationType)) {
      return moveOperation;
    }
    if (SPECIAL_ATTACK.equals(operationType)) {
      return specialAttackOperation;
    }
    if (STAND_UP.equals(operationType)) {
      return standUpOperation;
    }
    throw new IncorrectStateException("Couldn't find operation for " + operationType);
  }
}
