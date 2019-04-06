package dbryla.game.yetanotherengine.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dbryla.game.yetanotherengine.Presenter;
import dbryla.game.yetanotherengine.domain.Action;
import dbryla.game.yetanotherengine.domain.Game;
import dbryla.game.yetanotherengine.domain.IncorrectStateException;
import dbryla.game.yetanotherengine.domain.operations.Operation;
import dbryla.game.yetanotherengine.domain.subjects.classes.Subject;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
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
  private Presenter presenter;

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

  @Test
  void shouldReturnActionWithGivenOperation() throws IOException {
    when(bufferedReader.readLine()).thenReturn("0");
    Game game = mock(Game.class);
    when(game.getAllAliveEnemies()).thenReturn(List.of("enemy"));
    Operation operation = mock(Operation.class);
    Subject subject = mock(Subject.class);
    when(presenter.showAvailableOperations(any())).thenReturn(List.of(operation));

    Action action = consoleInputProvider.askForAction(subject, game);

    assertThat(action.getOperation()).isEqualTo(operation);
  }
}