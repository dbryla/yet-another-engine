package dbryla.game.yetanotherengine.telegram.commands;

import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.session.Session;
import dbryla.game.yetanotherengine.telegram.Communicate;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SpellCommandTest extends CommandTestSetup {

  @InjectMocks
  private SpellCommand spellCommand;

  @Test
  void shouldIgnoreCommandIfGameHasNotStarted() {
    Game game = mock(Game.class);
    when(game.isStarted()).thenReturn(false);
    when(sessionFactory.getGame(any())).thenReturn(game);
    Session session = mock(Session.class);
    when(sessionFactory.getSession(any())).thenReturn(session);

    spellCommand.execute(update);

    verifyZeroInteractions(telegramClient);
  }

  @Test
  void shouldIgnoreCommandIfGameHasEnded() {
    Game game = mock(Game.class);
    when(game.isStarted()).thenReturn(true);
    when(game.isEnded()).thenReturn(true);
    when(sessionFactory.getGame(any())).thenReturn(game);
    Session session = mock(Session.class);
    when(sessionFactory.getSession(any())).thenReturn(session);

    spellCommand.execute(update);

    verifyZeroInteractions(telegramClient);
  }

  @Test
  void shouldIgnoreCommandIfNotFromNextPlayer() {
    Game game = mock(Game.class);
    when(game.isStarted()).thenReturn(true);
    when(game.isEnded()).thenReturn(false);
    when(sessionFactory.getGame(any())).thenReturn(game);
    Session session = mock(Session.class);
    when(sessionFactory.getSession(any())).thenReturn(session);
    when(commons.isNextUser(any(), any())).thenReturn(false);

    spellCommand.execute(update);

    verifyZeroInteractions(telegramClient);
  }

  @Test
  void shouldSendCommunicateIfNoTargetsAreAvailable() {
    Game game = mock(Game.class);
    when(game.isStarted()).thenReturn(true);
    when(game.isEnded()).thenReturn(false);
    when(sessionFactory.getGame(any())).thenReturn(game);
    Session session = mock(Session.class);
    when(sessionFactory.getSession(any())).thenReturn(session);
    when(commons.isNextUser(any(), any())).thenReturn(true);
    when(fightFactory.spellCommunicate(any(), any())).thenReturn(new Communicate(null, List.of()));

    spellCommand.execute(update);

    verify(telegramClient).sendTextMessage(anyLong(), anyString());
  }

  @Test
  void shouldSendReplyKeyboardIfTargetsAreAvailable() {
    Game game = mock(Game.class);
    when(game.isStarted()).thenReturn(true);
    when(game.isEnded()).thenReturn(false);
    when(sessionFactory.getGame(any())).thenReturn(game);
    Session session = mock(Session.class);
    when(sessionFactory.getSession(any())).thenReturn(session);
    when(commons.isNextUser(any(), any())).thenReturn(true);
    Communicate communicate = new Communicate(null, List.of(List.of(mock(InlineKeyboardButton.class))));
    when(fightFactory.spellCommunicate(any(), any())).thenReturn(communicate);

    spellCommand.execute(update);

    verify(telegramClient).sendReplyKeyboard(eq(communicate), anyLong(), anyInt());
  }
}