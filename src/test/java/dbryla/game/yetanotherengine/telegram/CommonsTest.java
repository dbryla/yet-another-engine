package dbryla.game.yetanotherengine.telegram;

import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.game.SubjectTurn;
import dbryla.game.yetanotherengine.domain.subject.State;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.SubjectProperties;
import dbryla.game.yetanotherengine.session.FightSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommonsTest {

  @Mock
  private TelegramClient telegramClient;

  @InjectMocks
  private Commons commons;

  @Test
  void shouldReturnSessionId() {
    Message message = mock(Message.class);
    when(message.getChatId()).thenReturn(1L);
    User user = mock(User.class);
    when(user.getId()).thenReturn(2);

    String sessionId = commons.getSessionId(message, user);

    assertThat(sessionId).isEqualTo("1/2");
  }

  @Test
  void shouldReturnCharacterNameBasedOnUserName() {
    User user = mock(User.class);
    String userName = "alice";
    when(user.getFirstName()).thenReturn(userName);

    String sessionId = commons.getCharacterName(user);

    assertThat(sessionId).isEqualTo(userName);
  }

  @Test
  void shouldReturnTrueIfItIsNextUser() {
    String playerName = "playerName";
    Game game = mock(Game.class);
    when(game.getNextSubjectName()).thenReturn(Optional.of(playerName));

    assertThat(commons.isNextUser(playerName, game)).isTrue();
  }

  @Test
  void shouldReturnFalseIfItIsNotNextUser() {
    String playerName = "playerName";
    Game game = mock(Game.class);
    when(game.getNextSubjectName()).thenReturn(Optional.of("anotherPlayer"));

    assertThat(commons.isNextUser(playerName, game)).isFalse();
  }

  @Test
  void shouldExecuteTurn() {
    long chatId = 1L;
    int messageId = 2;
    Game game = mock(Game.class);
    SubjectTurn turn = mock(SubjectTurn.class);
    FightSession session = mock(FightSession.class);

    commons.executeTurnAndDeleteMessage(game, session, turn, chatId, messageId);

    verify(game).execute(eq(turn));
  }

  @Test
  void shouldDeleteMessage() {
    long chatId = 1L;
    int messageId = 2;
    Game game = mock(Game.class);
    SubjectTurn turn = mock(SubjectTurn.class);
    FightSession session = mock(FightSession.class);

    commons.executeTurnAndDeleteMessage(game, session, turn, chatId, messageId);

    verify(telegramClient).deleteMessage(eq(chatId), eq(messageId));
  }

  @Test
  void shouldCleanUpCallbackData() {
    long chatId = 1L;
    int messageId = 2;
    Game game = mock(Game.class);
    SubjectTurn turn = mock(SubjectTurn.class);
    FightSession session = mock(FightSession.class);

    commons.executeTurnAndDeleteMessage(game, session, turn, chatId, messageId);

    verify(session).cleanUpCallbackData();
  }

  @Test
  void shouldReturnTextAboutNextTurn() {
    Subject subject = mock(Subject.class);
    when(subject.isSpellCaster()).thenReturn(false);
    when(subject.isAbleToMove()).thenReturn(true);

    assertThat(commons.getPlayerTurnMessage(subject)).contains("/attack", "/move", "/pass");
  }

  @Test
  void shouldReturnTextAboutNextTurnWithSpellForSpellCaster() {
    Subject subject = mock(Subject.class);
    when(subject.isSpellCaster()).thenReturn(true);
    when(subject.isAbleToMove()).thenReturn(true);

    assertThat(commons.getPlayerTurnMessage(subject)).contains("/attack", "/move", "/pass", "/spell");
  }
}