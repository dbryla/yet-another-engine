package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.events.EventFactory;
import dbryla.game.yetanotherengine.domain.subject.State;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class MoveOperation implements Operation {

  private final EventFactory eventFactory;

  @Override
  public OperationResult invoke(Subject source, ActionData actionData, Subject... targets) throws UnsupportedGameOperationException {
    Position position = actionData.getPosition();
    verifyParams(source, position);
    State changedSubject = source.newState(position);
    return new OperationResult(changedSubject, eventFactory.movementEvent(changedSubject));
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
