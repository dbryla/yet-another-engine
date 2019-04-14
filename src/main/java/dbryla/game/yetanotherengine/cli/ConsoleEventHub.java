package dbryla.game.yetanotherengine.cli;

import dbryla.game.yetanotherengine.domain.events.Event;
import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("cli")
@Primary
public class ConsoleEventHub implements EventHub {

  @Override
  public void send(Long gameId, Event event) {
    System.out.println(event);
  }

  @Override
  public void notifySubjectAboutNextMove(Long gameId, Subject subject) {
    System.out.println(subject.getName() + " your turn!");
  }
}
