package dbryla.game.yetanotherengine.telegram.callback;

import static dbryla.game.yetanotherengine.telegram.CommunicateText.ABILITIES;
import static dbryla.game.yetanotherengine.telegram.CommunicateText.WEAPONS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dbryla.game.yetanotherengine.session.Session;
import dbryla.game.yetanotherengine.telegram.BuildingFactory;
import dbryla.game.yetanotherengine.telegram.Communicate;
import dbryla.game.yetanotherengine.telegram.SessionFactory;
import dbryla.game.yetanotherengine.telegram.TelegramClient;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class AbilitiesCallbackHandlerTest {

  @Mock
  private BuildingFactory buildingFactory;

  @Mock
  private SessionFactory sessionFactory;

  @Mock
  private TelegramClient telegramClient;

  @InjectMocks
  private AbilitiesCallbackHandler abilitiesCallbackHandler;

  @Test
  void shouldEditAbilitiesCommunicateAfterUserInteraction() {
    Session session = mock(Session.class);
    when(sessionFactory.getSession(any())).thenReturn(session);
    Callback callback = new Callback(0, "", 0L, "", "12", 0);
    when(session.getNextCommunicate()).thenReturn((new Communicate(ABILITIES, List.of())));
    when(buildingFactory.nextAbilityAssignment(any(), any())).thenReturn(Optional.empty());

    abilitiesCallbackHandler.execute(callback);

    verify(telegramClient).sendEditKeyboard(any(), anyLong(), any());
  }

  @Test
  void shouldDeleteOldMessageAndSendNewOneIfThereIsAnyCommunicate() {
    Session session = mock(Session.class);
    when(sessionFactory.getSession(any())).thenReturn(session);
    Callback callback = new Callback(0, "", 0L, "", "12", 0);
    when(session.getNextCommunicate()).thenReturn(new Communicate(WEAPONS, List.of()));
    when(buildingFactory.nextAbilityAssignment(any(), any())).thenReturn(Optional.empty());

    abilitiesCallbackHandler.execute(callback);

    verify(telegramClient).deleteMessage(anyLong(), any());
    verify(telegramClient).sendReplyKeyboard(any(), anyLong(), any());
  }

}