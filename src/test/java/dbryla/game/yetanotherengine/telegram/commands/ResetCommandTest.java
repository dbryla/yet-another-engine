package dbryla.game.yetanotherengine.telegram.commands;

import dbryla.game.yetanotherengine.telegram.session.GameStorage;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class ResetCommandTest extends CommandTestSetup {

  @InjectMocks
  private ResetCommand resetCommand;

  @Mock
  private GameStorage gameStorage;

  @Test
  void shouldClearGame() {
    resetCommand.execute(update);

    verify(gameStorage).clearGame(any());
  }
}