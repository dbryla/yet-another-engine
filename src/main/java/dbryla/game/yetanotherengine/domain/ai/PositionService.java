package dbryla.game.yetanotherengine.domain.ai;

import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.operations.ActionData;
import dbryla.game.yetanotherengine.domain.operations.MoveOperation;
import dbryla.game.yetanotherengine.domain.operations.OperationResult;
import dbryla.game.yetanotherengine.domain.operations.UnsupportedGameOperationException;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static dbryla.game.yetanotherengine.domain.battleground.Position.ENEMIES_FRONT;
import static dbryla.game.yetanotherengine.domain.battleground.Position.PLAYERS_BACK;

@Component
@AllArgsConstructor
public class PositionService { // fixme use with AI

  private final MoveOperation moveOperation;

  public Optional<OperationResult> adjustPosition(Subject source, Subject target, int minRange, int maxRange)
      throws UnsupportedGameOperationException {
    int distanceToTarget = Math.abs(source.getPosition().getBattlegroundLocation() - target.getPosition().getBattlegroundLocation());
    if (!isTargetInRange(distanceToTarget, minRange, maxRange)) {
      Optional<OperationResult> move = handleMissingDistanceToMinRange(source, minRange, distanceToTarget);
      if (move.isPresent()) {
        return move;
      }
      return handleMissingDistanceToMaxRange(source, maxRange, distanceToTarget);
    }
    return Optional.empty();
  }

  private boolean isTargetInRange(int distanceToTarget, int minRange, int maxRange) {
    return distanceToTarget >= minRange && distanceToTarget <= maxRange;
  }

  private Optional<OperationResult> handleMissingDistanceToMinRange(Subject source, int minRange, int distanceToTarget)
      throws UnsupportedGameOperationException {
    return moveIfPossible(source, minRange, distanceToTarget);
  }

  private Optional<OperationResult> handleMissingDistanceToMaxRange(Subject source, int maxRange, int distanceToTarget)
      throws UnsupportedGameOperationException {
    return moveIfPossible(source, maxRange, distanceToTarget);
  }

  private Optional<OperationResult> moveIfPossible(Subject source, int range, int distanceToTarget) throws UnsupportedGameOperationException {
    int missingDistanceToMinRange = distanceToTarget - range;
    if (missingDistanceToMinRange == 0) {
      return Optional.empty();
    }
    throwExceptionIfTargetIsTooFarAway(missingDistanceToMinRange);
    throwExceptionWhenMovingOutsideOfBattleground(source, missingDistanceToMinRange);
    return Optional.of(moveOperation
        .invoke(source, new ActionData(Position.valueOf(source.getPosition().getBattlegroundLocation() + missingDistanceToMinRange))));
  }

  private void throwExceptionIfTargetIsTooFarAway(int range) throws UnsupportedGameOperationException {
    if (Math.abs(range) > 1) {
      throw new UnsupportedGameOperationException("Target is too far away, can't move to perform operation on it.");
    }
  }

  private void throwExceptionWhenMovingOutsideOfBattleground(Subject source, int missingDistanceToRange) throws UnsupportedGameOperationException {
    if (PLAYERS_BACK.equals(source.getPosition()) && missingDistanceToRange < 0) {
      throw new UnsupportedGameOperationException("Can't move back to perform operation. Start of battleground.");
    }
    if (ENEMIES_FRONT.equals(source.getPosition()) && missingDistanceToRange > 0) {
      throw new UnsupportedGameOperationException("Can't move further to perform operation. End of battleground.");
    }
  }


}
