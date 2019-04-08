package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.events.EventsFactory;
import dbryla.game.yetanotherengine.domain.subjects.ActiveEffect;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EffectConsumer {

  private final EventHub eventHub;
  private final EventsFactory eventsFactory;

  Optional<Subject> apply(Subject source) {
    Optional<ActiveEffect> activeEffect = source.getActiveEffect();
    if (activeEffect.isPresent()) {
      activeEffect.get().decreaseDuration();
      if (activeEffect.get().getDurationInTurns() == 0) {
        eventHub.send(eventsFactory.effectExpiredEvent(source));
        return Optional.of(source.effectExpired());
      }
    }
    return Optional.empty();
  }

}
