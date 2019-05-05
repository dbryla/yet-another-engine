package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.effects.Effect;
import dbryla.game.yetanotherengine.domain.events.EventFactory;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class StandUpOperation implements Operation {

  private final EventFactory eventFactory;

  @Override
  public OperationResult invoke(Subject source, ActionData actionData, Subject... targets) throws UnsupportedGameOperationException {
    verifyParams(source);
    Subject changedSubject = source.effectExpired(Effect.PRONE);
    return new OperationResult(changedSubject, eventFactory.standUpEvent(changedSubject));
  }

  private void verifyParams(Subject source) throws UnsupportedGameOperationException {
    if (source == null) {
      throw new UnsupportedGameOperationException("Can't invoke operation on null source");
    }
  }

}
