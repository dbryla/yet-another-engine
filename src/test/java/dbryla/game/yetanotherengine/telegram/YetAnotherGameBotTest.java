package dbryla.game.yetanotherengine.telegram;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dbryla.game.yetanotherengine.telegram.commands.MoveCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@ExtendWith(MockitoExtension.class)
class YetAnotherGameBotTest {

  @Mock
  private MoveCommand moveCommand;

  @Mock
  private MoveCommand passCommand;

  @InjectMocks
  private YetAnotherGameBot yetAnotherGameBot;

  @Test
  void shouldExecuteMoveCommand() {
    Message message = mock(Message.class);
    when(message.getText()).thenReturn("/move");
    when(message.isCommand()).thenReturn(true);
    when(message.getChatId()).thenReturn(1L);
    when(message.getFrom()).thenReturn(new User());
    Update update = mock(Update.class);
    when(update.getMessage()).thenReturn(message);
    when(update.hasMessage()).thenReturn(true);

    yetAnotherGameBot.onUpdateReceived(update);

    verify(moveCommand).execute(eq(update));
  }

  @Test
  void shouldExecutePassCommand() {
    Message message = mock(Message.class);
    when(message.getText()).thenReturn("/pass");
    when(message.isCommand()).thenReturn(true);
    when(message.getChatId()).thenReturn(1L);
    when(message.getFrom()).thenReturn(new User());
    Update update = mock(Update.class);
    when(update.getMessage()).thenReturn(message);
    when(update.hasMessage()).thenReturn(true);

    yetAnotherGameBot.onUpdateReceived(update);

    verify(passCommand).execute(eq(update));
  }
}