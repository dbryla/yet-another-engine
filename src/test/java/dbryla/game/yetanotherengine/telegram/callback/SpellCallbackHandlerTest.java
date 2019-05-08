package dbryla.game.yetanotherengine.telegram.callback;

import static dbryla.game.yetanotherengine.telegram.CommunicateText.TARGETS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.telegram.Communicate;
import dbryla.game.yetanotherengine.telegram.FightFactory;
import dbryla.game.yetanotherengine.telegram.SessionFactory;
import dbryla.game.yetanotherengine.telegram.TelegramClient;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

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