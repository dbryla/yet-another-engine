package dbryla.game.yetanotherengine.domain.ai;

import static dbryla.game.yetanotherengine.domain.battleground.Position.ENEMIES_BACK;
import static dbryla.game.yetanotherengine.domain.battleground.Position.PLAYERS_BACK;

import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.game.Action;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.operations.ActionData;
import dbryla.game.yetanotherengine.domain.operations.OperationType;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
class PositionService {

  Optional<Action> adjustPosition(Subject source, Subject target, Game game, int minRange, int maxRange) {
    int distanceToTarget = Math.abs(source.getPosition().getBattlegroundLocation() - target.getPosition().getBattlegroundLocation());
    if (!isTargetInRange(distanceToTarget, minRange, maxRange)) {
      return handleMissingDistanceToMinRange(source, minRange, distanceToTarget, game)
          .or(() -> handleMissingDistanceToMaxRange(source, maxRange, distanceToTarget, game))
          .map(actionData -> new Action(source.getName(), target.getName(), OperationType.MOVE, actionData));
    }
    return Optional.empty();
  }

  private boolean isTargetInRange(int distanceToTarget, int minRange, int maxRange) {
    return distanceToTarget >= minRange && distanceToTarget <= maxRange;
  }

  private Optional<ActionData> handleMissingDistanceToMinRange(Subject source, int minRange, int distanceToTarget, Game game) {
    return actionData(source, minRange, distanceToTarget, game);
  }

  private Optional<ActionData> handleMissingDistanceToMaxRange(Subject source, int maxRange, int distanceToTarget, Game game) {
    return actionData(source, maxRange, distanceToTarget, game);
  }

  private Optional<ActionData> actionData(Subject source, int range, int distanceToTarget, Game game) {
    int missingDistanceToRange = (distanceToTarget - range) * source.getAffiliation().getDirection();
    if (missingDistanceToRange == 0
        || isTargetTooFarAway(missingDistanceToRange)
        || isMoveOutsideOfBattleground(source, missingDistanceToRange)
        || wouldNeedToPassEnemies(source, missingDistanceToRange, game)) {
      return Optional.empty();
    }
    return Optional.of(
        new ActionData(
            Position.valueOf(source.getPosition().getBattlegroundLocation() + missingDistanceToRange)));
  }

  private boolean isTargetTooFarAway(int range) {
    return Math.abs(range) > 1;
  }

  private boolean isMoveOutsideOfBattleground(Subject source, int missingDistanceToRange) {
    return PLAYERS_BACK.equals(source.getPosition()) && missingDistanceToRange < 0
        || ENEMIES_BACK.equals(source.getPosition()) && missingDistanceToRange > 0;
  }

  boolean wouldNeedToPassEnemies(Subject subject, int missingDistanceToRange, Game game) {
    if (missingDistanceToRange < 0) {
      return game.areEnemiesOnCurrentPosition(subject);
    }
    return false;
  }
}
