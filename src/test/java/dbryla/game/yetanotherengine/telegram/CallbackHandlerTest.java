package dbryla.game.yetanotherengine.telegram;

import dbryla.game.yetanotherengine.db.CharacterRepository;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.subject.SubjectFactory;
import dbryla.game.yetanotherengine.session.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CallbackHandlerTest {

  @InjectMocks
  private CallbackHandler callbackHandler;

  @Mock
  private SessionFactory sessionFactory;

  @Mock
  private TelegramClient telegramClient;

  @Mock
  private SubjectFactory subjectFactory;

  @Mock
  private Commons commons;

  @Mock
  private CharacterRepository characterRepository;

  @Test
  void shouldUpdateSessionWithCallbackData() {
    Message replyMessage = mock(Message.class);
    Message message = mock(Message.class);
    when(message.getReplyToMessage()).thenReturn(replyMessage);
    when(message.getText()).thenReturn("message-text");
    CallbackQuery callbackQuery = mock(CallbackQuery.class);
    when(callbackQuery.getData()).thenReturn("callback-data");
    when(callbackQuery.getMessage()).thenReturn(message);
    Update update = mock(Update.class);
    when(update.getCallbackQuery()).thenReturn(callbackQuery);
    when(commons.getCharacterName(any())).thenReturn("player");
    Session session = mock(Session.class);
    when(sessionFactory.getSession(any())).thenReturn(session);
    Game game = mock(Game.class);
    when(sessionFactory.getGameOrCreate(any())).thenReturn(game);

    callbackHandler.execute(update);

    verify(sessionFactory).updateSession(any(), any(), eq("callback-data"));
  }
}