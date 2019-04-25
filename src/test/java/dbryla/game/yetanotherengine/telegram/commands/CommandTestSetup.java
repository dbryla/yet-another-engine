package dbryla.game.yetanotherengine.telegram.commands;

import dbryla.game.yetanotherengine.telegram.Commons;
import dbryla.game.yetanotherengine.telegram.FightFactory;
import dbryla.game.yetanotherengine.telegram.SessionFactory;
import dbryla.game.yetanotherengine.telegram.TelegramClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public abstract class CommandTestSetup {

  @Mock
  protected TelegramClient telegramClient;

  @Mock
  protected SessionFactory sessionFactory;

  @Mock
  protected FightFactory fightFactory;

  @Mock
  protected Commons commons;

  @Mock
  protected Update update;

  @Mock
  protected Message message;

  @BeforeEach
  protected void setUp() {
    when(update.getMessage()).thenReturn(message);
  }
}
