package dbryla.game.yetanotherengine.cli;

import dbryla.game.yetanotherengine.domain.IncorrectStateException;
import java.io.BufferedReader;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Profile("cli")
@Component
public class ConsoleInputProvider {

  private final BufferedReader input;

  int cmdLineToOption() {
    try {
      return Integer.valueOf(input.readLine());
    } catch (IOException e) {
      throw new IncorrectStateException("Exception while reading cmdline option.", e);
    }
  }

  String cmdLine() {
    try {
      return input.readLine();
    } catch (IOException e) {
      throw new IncorrectStateException("Exception while reading cmdline option.", e);
    }
  }


}
