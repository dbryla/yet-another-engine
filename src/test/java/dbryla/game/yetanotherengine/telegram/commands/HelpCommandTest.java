package dbryla.game.yetanotherengine.telegram.commands;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

class HelpCommandTest extends CommandTestSetup {

  @InjectMocks
  private HelpCommand helpCommand;

  @Test
  void shouldSendManualToClient() {
    helpCommand.execute(update);

    verify(telegramClient).sendTextMessage(any(), anyString());
  }
}