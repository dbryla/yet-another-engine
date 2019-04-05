package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.events.EventsFactory;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EffectConsumer {

  private final EventHub eventHub;
  private final EventsFactory eventsFactory;

  public Optional<Subject> apply(Subject source) {
    if (source.getActiveEffect().isPresent()) {
      source.decreaseDurationOfActiveEffect();
      if (source.getActiveEffectDurationInTurns() == 0) {
        eventHub.send(eventsFactory.effectExpiredEvent(source));
        return Optional.of(source.effectExpired());
      }
    }
    return Optional.empty();
  }

}
