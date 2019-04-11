package dbryla.game.yetanotherengine.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import dbryla.game.yetanotherengine.domain.IncorrectStateException;
import java.io.BufferedReader;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConsoleInputProviderTest {

  @InjectMocks
  private ConsoleInputProvider consoleInputProvider;

  @Mock
  private BufferedReader bufferedReader;

  @Mock
  private ConsolePresenter presenter;

  @Test
  void shouldTransformCmdlineToInteger() throws IOException {
    when(bufferedReader.readLine()).thenReturn("1");

    int option = consoleInputProvider.cmdLineToOption();

    assertThat(option).isEqualTo(1);
  }

  @Test
  void shouldReadCmdline() throws IOException {
    when(bufferedReader.readLine()).thenReturn("text");

    String cmdLine = consoleInputProvider.cmdLine();

    assertThat(cmdLine).isEqualTo("text");
  }

  @Test
  void shouldEncapsulateExceptionForCmdLineToOption() throws IOException {
    when(bufferedReader.readLine()).thenThrow(new IOException());

    assertThrows(IncorrectStateException.class, () -> consoleInputProvider.cmdLineToOption());
  }

  @Test
  void shouldEncapsulateExceptionForCmdLine() throws IOException {
    when(bufferedReader.readLine()).thenThrow(new IOException());

    assertThrows(IncorrectStateException.class, () -> consoleInputProvider.cmdLine());
  }
}