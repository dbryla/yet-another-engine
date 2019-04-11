package dbryla.game.yetanotherengine.domain.events;

import dbryla.game.yetanotherengine.domain.subjects.Subject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoggingEventHub implements EventHub {

  @Override
  public void send(Event event, Long gameId) {
    log.info("{}", event.toString());
  }

  @Override
  public void nextMove(Subject subject, Long gameId) {
    log.info("Next move: {} for game {}", subject.getName(), gameId);
  }
}
