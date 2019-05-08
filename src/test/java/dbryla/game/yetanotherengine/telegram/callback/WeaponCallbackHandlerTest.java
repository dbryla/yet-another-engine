package dbryla.game.yetanotherengine.telegram.callback;

import static dbryla.game.yetanotherengine.telegram.CommunicateText.TARGETS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dbryla.game.yetanotherengine.session.Session;
import dbryla.game.yetanotherengine.telegram.Communicate;
import dbryla.game.yetanotherengine.telegram.FightFactory;
import dbryla.game.yetanotherengine.telegram.SessionFactory;
import dbryla.game.yetanotherengine.telegram.TelegramClient;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class WeaponCallbackHandlerTest {

  @Mock
  private FightFactory fightFactory;

  @Mock
  private SessionFactory sessionFactory;

  @Mock
  private TelegramClient telegramClient;

  @InjectMocks
  private WeaponCallbackHandler weaponCallbackHandler;

  @Test
  void shouldHandleWeaponCallbackRemoveMessageAndAskForTarget() {
    Callback callback = new Callback(0, "player", 0L, "", "LONGSWORD", 0);
    Session session = mock(Session.class);
    when(sessionFactory.getSession(any())).thenReturn(session);
    when(fightFactory.targetCommunicate(any(), any(), any())).thenReturn(Optional.of(new Communicate(TARGETS, List.of())));

    weaponCallbackHandler.execute(callback);

    verify(telegramClient).deleteMessage(any(), any());
    verify(telegramClient).sendReplyKeyboard(any(), any(), any());
  }

}