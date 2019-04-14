package dbryla.game.yetanotherengine.telegram;

import dbryla.game.yetanotherengine.domain.events.Event;
import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.game.GameOptions;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("tg")
@Primary
@AllArgsConstructor
public class TelegramEventHub implements EventHub {

  private final EventHub loggingEventHub;
  private final TelegramClient telegramClient;

  @Override
  public void send(Long gameId, Event event) {
    loggingEventHub.send(gameId, event);
    telegramClient.sendTextMessage(gameId, event.toString());
  }

  @Override
  public void notifySubjectAboutNextMove(Long gameId, Subject subject) {
    loggingEventHub.notifySubjectAboutNextMove(gameId, subject);
    telegramClient.sendTextMessage(gameId, subject.getName() + " your move: /attack" + getSpellCommandIfApplicable(subject));
  }

  private String getSpellCommandIfApplicable(Subject subject) {
    return subject.isSpellCaster() ? " or /spell" : "";
}
}
