package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.events.EventFactory;

import dbryla.game.yetanotherengine.domain.subject.Subject;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EffectConsumer {

  private final EventFactory eventFactory;

  public OperationResult apply(Subject source) {
    OperationResult operationResult = new OperationResult();
    source.getActiveEffects().forEach(
        activeEffect -> {
          if (activeEffect.getDurationInTurns() > 0) {
            activeEffect.decreaseDuration();
            if (activeEffect.getDurationInTurns() == 0) {
              operationResult.add(
                  source.effectExpired(activeEffect.getEffect()),
                  eventFactory.effectExpiredEvent(source, activeEffect.getEffect()));
            }
          }
        }
    );
    return operationResult;
  }

}
