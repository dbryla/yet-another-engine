package dbryla.game.yetanotherengine.telegram.commands;

import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.SubjectProperties;
import dbryla.game.yetanotherengine.session.FightSession;
import dbryla.game.yetanotherengine.telegram.Communicate;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MoveCommandTest extends CommandTestSetup {

  @InjectMocks
  private MoveCommand moveCommand;

  @Test
  void shouldSendReplyKeyboardToTakeNewPosition() {
    User user = mock(User.class);
    long chatId = 1L;
    Integer messageId = 2;
    when(message.getChatId()).thenReturn(chatId);
    when(message.getFrom()).thenReturn(user);
    when(message.getMessageId()).thenReturn(messageId);
    Subject subject = mock(Subject.class);
    Game game = mock(Game.class);
    when(game.getSubject(any())).thenReturn(subject);
    SubjectProperties subjectProperties = mock(SubjectProperties.class);
    when(sessionFactory.getFightSession(any())).thenReturn(new FightSession("player", subjectProperties));
    when(sessionFactory.getGame(eq(chatId))).thenReturn(game);
    when(fightFactory.moveCommunicate(any(), any())).thenReturn(Optional.of(new Communicate(null, null)));

    moveCommand.execute(update);

    verify(telegramClient).sendReplyKeyboard(any(), eq(chatId), eq(messageId));
    verifyNoMoreInteractions(telegramClient);
  }

  @Test
  void shouldSendCommunicateAboutNotBeingAbleToMove() {
    User user = mock(User.class);
    long chatId = 1L;
    when(message.getChatId()).thenReturn(chatId);
    when(message.getFrom()).thenReturn(user);
    Subject subject = mock(Subject.class);
    Game game = mock(Game.class);
    when(game.getSubject(any())).thenReturn(subject);
    SubjectProperties subjectProperties = mock(SubjectProperties.class);
    when(sessionFactory.getFightSession(any())).thenReturn(new FightSession("player", subjectProperties));
    when(sessionFactory.getGame(eq(chatId))).thenReturn(game);

    moveCommand.execute(update);

    verify(telegramClient).sendTextMessage(eq(chatId), eq("You are unable to move right now."));
    verifyNoMoreInteractions(telegramClient);
  }

  @Test
  void shouldNotUpdateSessionWhenPlayerTriesToMoveOnCurrentPosition() {
    User user = mock(User.class);
    when(message.getFrom()).thenReturn(user);

    moveCommand.execute(update);

    verify(telegramClient).sendTextMessage(any(), anyString());
    verifyNoMoreInteractions(telegramClient);
  }

  @Test
  void shouldNotUpdateSessionWhenFightHasNotStarted() {
    long chatId = 1L;
    User user = mock(User.class);
    when(message.getChatId()).thenReturn(chatId);
    when(message.getFrom()).thenReturn(user);
    Game game = mock(Game.class);
    when(sessionFactory.getGame(eq(chatId))).thenReturn(game);

    moveCommand.execute(update);

    verify(telegramClient).sendTextMessage(any(), anyString());
    verifyNoMoreInteractions(telegramClient);
  }
}