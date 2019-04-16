package dbryla.game.yetanotherengine.telegram.commands;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.session.Session;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.telegram.telegrambots.meta.api.objects.User;

class MoveCommandTest extends CommandTestSetup {

  @InjectMocks
  private MoveCommand moveCommand;

  @Test
  void shouldNotChangeSessionWhenCommandDoesNotHaveTwoArguments() {
    long chatId = 1L;
    when(message.getChatId()).thenReturn(chatId);
    when(message.getText()).thenReturn("/movefront");

    moveCommand.execute(update);

    verify(telegramClient).sendTextMessage(eq(chatId), any());
    verifyZeroInteractions(sessionFactory);
  }

  @Test
  void shouldUpdateSessionWithNewPosition() {
    long chatId = 1L;
    User user = mock(User.class);
    when(user.getId()).thenReturn(8);
    when(message.getChatId()).thenReturn(chatId);
    when(message.getText()).thenReturn("/move front");
    when(message.getFrom()).thenReturn(user);
    Subject subject = mock(Subject.class);
    when(subject.getPosition()).thenReturn(Position.PLAYERS_BACK);
    Game game = mock(Game.class);
    when(game.isStarted()).thenReturn(false);
    when(sessionFactory.getSession(any())).thenReturn(new Session("player", subject));
    when(sessionFactory.getGame(eq(chatId))).thenReturn(game);

    moveCommand.execute(update);

    verify(telegramClient).sendTextMessage(eq(chatId), any());
    verify(game).moveSubject(any(), any());
  }

  @Test
  void shouldNotUpdateSessionWhenPlayerTriesToMoveOnCurrentPosition() {
    long chatId = 1L;
    User user = mock(User.class);
    when(user.getId()).thenReturn(8);
    when(message.getChatId()).thenReturn(chatId);
    when(message.getText()).thenReturn("/move back");
    when(message.getFrom()).thenReturn(user);
    Subject subject = mock(Subject.class);
    when(subject.getPosition()).thenReturn(Position.PLAYERS_BACK);
    Game game = mock(Game.class);
    when(game.isStarted()).thenReturn(false);
    when(sessionFactory.getSession(any())).thenReturn(new Session("player", subject));
    when(sessionFactory.getGame(eq(chatId))).thenReturn(game);

    moveCommand.execute(update);

    verifyZeroInteractions(telegramClient);
    verify(game, times(0)).moveSubject(any(), any());
  }

  @Test
  void shouldNotUpdateSessionWhenFightHasStarted() {
    long chatId = 1L;
    when(message.getChatId()).thenReturn(chatId);
    when(message.getText()).thenReturn("/move front");
    Game game = mock(Game.class);
    when(game.isStarted()).thenReturn(true);
    when(sessionFactory.getGame(eq(chatId))).thenReturn(game);

    moveCommand.execute(update);

    verify(telegramClient).sendTextMessage(any(), anyString());
    verify(game, times(0)).moveSubject(any(), any());
  }
}