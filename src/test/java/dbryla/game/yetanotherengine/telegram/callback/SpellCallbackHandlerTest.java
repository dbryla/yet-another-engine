package dbryla.game.yetanotherengine.telegram.callback;

import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.telegram.Communicate;
import dbryla.game.yetanotherengine.telegram.FightFactory;
import dbryla.game.yetanotherengine.telegram.SessionFactory;
import dbryla.game.yetanotherengine.telegram.TelegramClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static dbryla.game.yetanotherengine.telegram.CommunicateText.TARGETS;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpellCallbackHandlerTest {

  @Mock
  private FightFactory fightFactory;

  @Mock
  private SessionFactory sessionFactory;

  @Mock
  private TelegramClient telegramClient;

  @InjectMocks
  private SpellCallbackHandler spellCallbackHandler;


  @Test
  void shouldHandleSpellCallbackRemoveMessageAndAskForTarget() {
    Game game = mock(Game.class);
    when(sessionFactory.getGame(anyLong())).thenReturn(game);
    when(game.getPossibleTargets(anyString(), eq(Spell.SACRED_FLAME))).thenReturn(List.of("target1", "target2"));
    when(fightFactory.targetCommunicate(any())).thenReturn(Optional.of(new Communicate(TARGETS, List.of())));
    Callback callback = new Callback(0, "player", 0L, "", "SACRED_FLAME", 0);

    spellCallbackHandler.execute(callback);

    verify(telegramClient).deleteMessage(any(), any());
    verify(telegramClient).sendReplyKeyboard(any(), any(), any());
  }

}