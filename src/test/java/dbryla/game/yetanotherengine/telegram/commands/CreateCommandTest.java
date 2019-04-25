package dbryla.game.yetanotherengine.telegram.commands;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

class CreateCommandTest extends CommandTestSetup {

  @InjectMocks
  private CreateCommand createCommand;

  @Mock
  private JoinCommand joinCommand;

  @Test
  void shouldDelegateCreateCommandToJoinCommand() {
    createCommand.execute(update);

    verify(joinCommand).createNewSessionAndCharacter(eq(message), any(), any());
  }
}