package dbryla.game.yetanotherengine.telegram.commands;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class StartCommandTest extends CommandTestSetup {

  @InjectMocks
  private StartCommand startCommand;

  @Test
  void shouldCreateGame() {
    startCommand.execute(update);

    verify(sessionFactory).getGameOrCreate(any());
  }
}