package dbryla.game.yetanotherengine.telegram.callback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dbryla.game.yetanotherengine.domain.game.SubjectTurn;
import dbryla.game.yetanotherengine.domain.operations.OperationType;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.session.Session;
import dbryla.game.yetanotherengine.telegram.Commons;
import dbryla.game.yetanotherengine.telegram.SessionFactory;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class TargetsCallbackHandlerTest {

  @Mock
  private SessionFactory sessionFactory;

  @Mock
  private Commons commons;

  @InjectMocks
  private TargetsCallbackHandler targetsCallbackHandler;

  @Test
  void shouldExecuteTurnWithSpellCastOnGivenTarget() {
    Callback callback = new Callback(0, "player", 0L, "", "target1", 0);
    Session session = mock(Session.class);
    when(sessionFactory.getSession(any())).thenReturn(session);
    when(session.isSpellCasting()).thenReturn(true);
    when(session.getSpell()).thenReturn(Spell.SACRED_FLAME);
    when(session.areAllTargetsAcquired()).thenReturn(true);

    targetsCallbackHandler.execute(callback);

    ArgumentCaptor<SubjectTurn> captor = ArgumentCaptor.forClass(SubjectTurn.class);
    verify(commons).executeTurnAndDeleteMessage(any(), any(), captor.capture(), any(), any());
    SubjectTurn turn = captor.getValue();
    assertThat(turn.getActions()).isNotEmpty();
    assertThat(turn.getActions().get(0).getOperationType()).isEqualTo(OperationType.SPELL_CAST);
  }

  @Test
  void shouldExecuteTurnWithAttackOnGivenTarget() {
    Callback callback = new Callback(0, "player", 0L, "", "target1", 0);
    Session session = mock(Session.class);
    when(sessionFactory.getSession(any())).thenReturn(session);
    when(session.isSpellCasting()).thenReturn(false);

    targetsCallbackHandler.execute(callback);

    ArgumentCaptor<SubjectTurn> captor = ArgumentCaptor.forClass(SubjectTurn.class);
    verify(commons).executeTurnAndDeleteMessage(any(), any(), captor.capture(), any(), any());
    SubjectTurn turn = captor.getValue();
    assertThat(turn.getActions()).isNotEmpty();
    assertThat(turn.getActions().get(0).getOperationType()).isEqualTo(OperationType.ATTACK);
  }


}