package dbryla.game.yetanotherengine.telegram.commands;

import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.session.FightSession;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PassCommandTest extends CommandTestSetup {

  @InjectMocks
  private PassCommand passCommand;

  @Test
  void shouldExecutePassIfGameHasStarted() {
    when(sessionFactory.getFightSession(any())).thenReturn(mock(FightSession.class));
    Game game = mock(Game.class);
    when(sessionFactory.getGame(any())).thenReturn(game);
    when(game.isStarted()).thenReturn(true);
    when(commons.isNextUser(any(), any())).thenReturn(true);

    passCommand.execute(update);

    verify(commons).executeTurn(any(), any(), any());
  }

  @Test
  void shouldIgnoreCommandIfNoGameHasStarted() {
    when(sessionFactory.getFightSession(any())).thenReturn(mock(FightSession.class));
    Game game = mock(Game.class);
    when(sessionFactory.getGame(any())).thenReturn(game);
    when(game.isStarted()).thenReturn(false);

    passCommand.execute(update);

    verify(commons).getSessionId(any(), any());
    verifyNoMoreInteractions(commons);

  }
}