package dbryla.game.yetanotherengine.domain.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoggingEventHub implements EventHub {
  @Override
  public void send(Event event) {
    log.info(event.toString());
  }
}
