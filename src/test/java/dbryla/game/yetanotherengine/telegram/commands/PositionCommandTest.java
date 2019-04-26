package dbryla.game.yetanotherengine.telegram.commands;

import dbryla.game.yetanotherengine.domain.game.Game;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PositionCommandTest extends CommandTestSetup {

  @InjectMocks
  private PositionCommand positionCommand;

  @Test
  void shouldSendPositionsBasedOnPositionsMapWhenGameExists() {
    Game game = mock(Game.class);
    when(sessionFactory.getGame(any())).thenReturn(game);

    positionCommand.execute(update);

    verify(game).getSubjectsPositionsMap();
    verify(telegramClient).sendTextMessage(any(), any());
  }

  @Test
  void shouldSendMessageWhenGameHasDoesNotExist() {
    positionCommand.execute(update);

    verify(telegramClient).sendTextMessage(any(), any());
  }
}