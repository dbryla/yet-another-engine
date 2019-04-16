package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.dice.DiceRollService;
import dbryla.game.yetanotherengine.domain.events.Event;
import dbryla.game.yetanotherengine.domain.events.EventsFactory;
import dbryla.game.yetanotherengine.domain.game.state.storage.StateStorage;
import dbryla.game.yetanotherengine.domain.subject.Abilities;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@Component
public class MoveOperation {

  private final EventsFactory eventsFactory;

  public OperationResult invoke(Subject source, ActionData actionData) throws UnsupportedGameOperationException {
    Position position = actionData.getPosition();
    verifyParams(source, position);
    return new OperationResult(source.of(position), eventsFactory.movementEvent(source));
  }

  private void verifyParams(Subject source, Position position) throws UnsupportedGameOperationException {
    if (source == null) {
      throw new UnsupportedGameOperationException("Can't invoke operation on null source");
    }
    if (position == null) {
      throw new UnsupportedGameOperationException("Can't move on null position.");
    }
  }

}
