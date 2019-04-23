package dbryla.game.yetanotherengine.domain.ai;

import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.game.Action;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.operations.ActionData;
import dbryla.game.yetanotherengine.domain.operations.OperationType;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static dbryla.game.yetanotherengine.domain.battleground.Position.ENEMIES_FRONT;
import static dbryla.game.yetanotherengine.domain.battleground.Position.PLAYERS_BACK;

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
    int missingDistanceToRange = distanceToTarget - range;
    if (missingDistanceToRange == 0
        || isTargetTooFarAway(missingDistanceToRange)
        || isMoveOutsideOfBattleground(source, missingDistanceToRange)
        || wouldNeedToPassEnemies(source, missingDistanceToRange, game)) {
      return Optional.empty();
    }
    return Optional.of(new ActionData(Position.valueOf(source.getPosition().getBattlegroundLocation() + missingDistanceToRange)));
  }

  private boolean isTargetTooFarAway(int range) {
    return Math.abs(range) > 1;
  }

  private boolean isMoveOutsideOfBattleground(Subject source, int missingDistanceToRange) {
    return PLAYERS_BACK.equals(source.getPosition()) && missingDistanceToRange < 0
        || ENEMIES_FRONT.equals(source.getPosition()) && missingDistanceToRange > 0;
  }

  boolean wouldNeedToPassEnemies(Subject subject, int missingDistanceToRange, Game game) {
    if (missingDistanceToRange * subject.getAffiliation().getDirection() < 0) {
      return game.areEnemiesOnCurrentPosition(subject);
    }
    return false;
  }
}
