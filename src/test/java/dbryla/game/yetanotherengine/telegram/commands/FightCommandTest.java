package dbryla.game.yetanotherengine.telegram.commands;

import dbryla.game.yetanotherengine.domain.encounters.MonstersFactory;
import dbryla.game.yetanotherengine.domain.game.Game;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FightCommandTest extends CommandTestSetup {

  @InjectMocks
  private FightCommand fightCommand;

  @Mock
  private MonstersFactory monstersFactory;

  @Test
  void shouldSendCommunicateToJoinGameFirst() {
    fightCommand.execute(update);

    verify(telegramClient).sendTextMessage(any(), eq("Please start game first!"));
  }

  @Test
  void shouldSendCommunicateThatGameAlreadyStarted() {
    Game game = mock(Game.class);
    when(game.isStarted()).thenReturn(true);
    when(sessionFactory.getGame(anyLong())).thenReturn(game);

    fightCommand.execute(update);

    verify(telegramClient).sendTextMessage(any(), eq("Game already started!"));
  }

  @Test
  void shouldSendCommunicateIfNoPlayersJoinedTheGame() {
    Game game = mock(Game.class);
    when(game.isStarted()).thenReturn(false);
    when(game.getPlayersNumber()).thenReturn(0);
    when(sessionFactory.getGame(anyLong())).thenReturn(game);

    fightCommand.execute(update);

    verify(telegramClient).sendTextMessage(any(), eq("Please join game before starting fight!"));
  }

  @Test
  void shouldCreateMonstersAndStartGame() {
    Game game = mock(Game.class);
    when(game.isStarted()).thenReturn(false);
    when(game.getPlayersNumber()).thenReturn(1);
    when(sessionFactory.getGame(anyLong())).thenReturn(game);
    when(monstersFactory.createEncounter(anyInt())).thenReturn(List.of());

    fightCommand.execute(update);

    verifyZeroInteractions(telegramClient);
    verify(game).createNonPlayableCharacters(anyList());
    verify(game).start();
  }
}