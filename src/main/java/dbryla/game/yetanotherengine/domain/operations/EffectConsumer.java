package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.events.EventsFactory;
import dbryla.game.yetanotherengine.domain.subjects.ActiveEffect;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EffectConsumer {

  private final EventsFactory eventsFactory;

  Optional<OperationResult> apply(Subject source) {
    Optional<ActiveEffect> activeEffect = source.getActiveEffect();
    if (activeEffect.isPresent()) {
      activeEffect.get().decreaseDuration();
      if (activeEffect.get().getDurationInTurns() == 0) {
        return Optional.of(new OperationResult(Set.of(source.effectExpired()), Set.of(eventsFactory.effectExpiredEvent(source))));
      }
    }
    return Optional.empty();
  }

}
