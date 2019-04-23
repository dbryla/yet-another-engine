package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.events.EventsFactory;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class MoveOperation {

  private final EventsFactory eventsFactory;

  public OperationResult invoke(Subject source, ActionData actionData) throws UnsupportedGameOperationException {
    Position position = actionData.getPosition();
    verifyParams(source, position);
    Subject changedSubject = source.of(position);
    return new OperationResult(changedSubject, eventsFactory.movementEvent(changedSubject));
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
