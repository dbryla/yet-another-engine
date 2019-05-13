package dbryla.game.yetanotherengine.domain.ai;

import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.game.Action;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.operations.ActionData;
import dbryla.game.yetanotherengine.domain.operations.OperationType;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.State;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static dbryla.game.yetanotherengine.domain.battleground.Position.ENEMIES_BACK;
import static dbryla.game.yetanotherengine.domain.battleground.Position.PLAYERS_BACK;

@Component
@AllArgsConstructor
class PositionService {

  Optional<Action> adjustPosition(Subject source, String targetName, Game game, int minRange, int maxRange) {
    Subject target = game.getSubject(targetName);
    int distanceToTarget =
        Math.abs(source.getPosition().getBattlegroundLocation() - target.getPosition().getBattlegroundLocation());
    if (!isTargetInRange(distanceToTarget, minRange, maxRange)) {
      return actionData(source, target, minRange, distanceToTarget, game)
          .or(() -> actionData(source, target, maxRange, distanceToTarget, game))
          .map(actionData -> new Action(source.getName(), targetName, OperationType.MOVE, actionData));
    }
    return Optional.empty();
  }

  private boolean isTargetInRange(int distanceToTarget, int minRange, int maxRange) {
    return distanceToTarget >= minRange && distanceToTarget <= maxRange;
  }

  private Optional<ActionData> actionData(Subject source, Subject target, int range, int distanceToTarget, Game game) {
    int missingDistanceToRange = (distanceToTarget - range) * source.getAffiliation().getDirection();
    if (missingDistanceToRange == 0
        || isTargetTooFarAway(missingDistanceToRange)
        || isMoveOutsideOfBattleground(source, missingDistanceToRange)
        || wouldNeedToPassEnemies(source, target.getPosition(), game)
        || wouldNeedToMoveToFrighteningSubject(source, target.getPosition(), game)) {
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

  boolean wouldNeedToPassEnemies(Subject subject, Position newPosition, Game game) {
    if (game.isAheadOfSubject(subject, newPosition)) {
      return game.areEnemiesOnCurrentPosition(subject);
    }
    return false;
  }

  boolean wouldNeedToMoveToFrighteningSubject(Subject subject, Position newPosition, Game game) {
    if (game.isAheadOfSubject(subject, newPosition)) {
      return !game.isNotFrightenedAnyOneAhead(subject);
    }
    return false;
  }


}
