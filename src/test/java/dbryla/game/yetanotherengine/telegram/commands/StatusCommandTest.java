package dbryla.game.yetanotherengine.telegram.commands;

import dbryla.game.yetanotherengine.domain.game.Game;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StatusCommandTest extends CommandTestSetup {

  @InjectMocks
  private StatusCommand statusCommand;

  @Test
  void shouldSendMessageWhenGameDoesNotExist() {
    statusCommand.execute(update);

    verify(telegramClient).sendTextMessage(any(), any());
  }


  @Test
  void shouldSendStatusWithAllSubjectsWhenGameExists() {
    Game game = mock(Game.class);
    when(sessionFactory.getGame(any())).thenReturn(game);

    statusCommand.execute(update);

    verify(game).getAllSubjects();
    verify(telegramClient).sendTextMessage(any(), any());
  }
}