package dbryla.game.yetanotherengine.domain.events;

import dbryla.game.yetanotherengine.domain.subject.Subject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoggingEventHub implements EventHub {

  @Override
  public void send(Long gameId, Event event) {
    log.info("Game[{}]: {}", gameId, event.toString());
  }

  @Override
  public void notifySubjectAboutNextMove(Long gameId, Subject subject) {
    log.info("Game[{}]: Next move: {}", gameId, subject.getName());
  }
}
