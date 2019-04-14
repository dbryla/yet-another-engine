package dbryla.game.yetanotherengine.cli;

import dbryla.game.yetanotherengine.domain.encounters.MonstersFactory;
import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.game.GameFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CliTest {

  @InjectMocks
  private Cli cli;

  @Mock
  private Simulator simulator;

  @Mock
  private GameFactory gameFactory;

  @Mock
  private ConsoleCharacterBuilder consoleCharacterBuilder;

  @Mock
  private ConsolePresenter presenter;

  @Mock
  private ConsoleInputProvider inputProvider;

  @Mock
  private MonstersFactory monstersFactory;

  @Mock
  private EventHub eventHub;

  @Test
  void shouldRunSimulation() throws Exception {
    cli.run(Cli.SIMULATION_OPTION);

    verify(simulator).start();
  }

  @Test
  void shouldRunGame() throws Exception {
    Game game = mock(Game.class);
    when(game.isEnded()).thenReturn(true);
    when(gameFactory.newGame(anyLong())).thenReturn(game);

    cli.run(Cli.GAME_OPTION);

    verify(game).start();
  }
}