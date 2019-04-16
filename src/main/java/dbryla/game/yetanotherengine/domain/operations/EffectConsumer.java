package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.events.EventsFactory;
import dbryla.game.yetanotherengine.domain.subject.ActiveEffect;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import dbryla.game.yetanotherengine.domain.subject.Subject;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EffectConsumer {

  private final EventsFactory eventsFactory;

  public OperationResult apply(Subject source) {
    OperationResult operationResult = new OperationResult();
    source.getActiveEffects().forEach(
        activeEffect -> {
          if (activeEffect.getDurationInTurns() > 0) {
            activeEffect.decreaseDuration();
            if (activeEffect.getDurationInTurns() == 0) {
              operationResult.addAll(
                  Set.of(source.effectExpired(activeEffect.getEffect())),
                  Set.of(eventsFactory.effectExpiredEvent(source, activeEffect.getEffect())));
            }
          }
        }
    );
    return operationResult;
  }

}
