package dbryla.game.yetanotherengine.telegram.callback;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import dbryla.game.yetanotherengine.telegram.Commons;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@ExtendWith(MockitoExtension.class)
class CallbackServiceTest {

  @InjectMocks
  private CallbackService callbackService;

  @Mock
  private CallbackHandlerFactory callbackHandlerFactory;

  @Mock
  private Commons commons;

  @Mock
  private Update update;

  @Test
  void shouldNotDelegateIfCallbackFromAnotherPlayer() {
    Message replyMessage = mock(Message.class);
    Message message = mock(Message.class);
    when(message.getReplyToMessage()).thenReturn(replyMessage);
    when(message.getText()).thenReturn("test");
    CallbackQuery callbackQuery = mock(CallbackQuery.class);
    when(callbackQuery.getMessage()).thenReturn(message);
    when(update.getCallbackQuery()).thenReturn(callbackQuery);
    when(commons.getCharacterName(any())).thenReturn("player").thenReturn("anotherPlayer");

    callbackService.execute(update);

    verifyZeroInteractions(callbackHandlerFactory);
  }
}