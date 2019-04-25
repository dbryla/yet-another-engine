package dbryla.game.yetanotherengine.cli;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("cli")
class SimulatorITest {

  @Autowired
  private Simulator simulator;

  @Test
  void shouldRunAndFinishSimulator() {
    simulator.start();
  }
}