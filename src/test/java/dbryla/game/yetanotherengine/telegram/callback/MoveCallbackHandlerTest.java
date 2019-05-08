package dbryla.game.yetanotherengine.telegram.callback;

import dbryla.game.yetanotherengine.domain.game.SubjectTurn;
import dbryla.game.yetanotherengine.domain.operations.OperationType;
import dbryla.game.yetanotherengine.session.Session;
import dbryla.game.yetanotherengine.telegram.Commons;
import dbryla.game.yetanotherengine.telegram.SessionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MoveCallbackHandlerTest {

  @Mock
  private SessionFactory sessionFactory;

  @Mock
  private Commons commons;

  @InjectMocks
  private MoveCallbackHandler moveCallbackHandler;

  @Test
  void shouldExecuteTurnWithMove() {
    Callback callback = new Callback(0, "", 0L, "", "3", 0);
    Session session = mock(Session.class);
    when(sessionFactory.getSession(any())).thenReturn(session);
    when(session.isMoving()).thenReturn(true);

    moveCallbackHandler.execute(callback);

    ArgumentCaptor<SubjectTurn> captor = ArgumentCaptor.forClass(SubjectTurn.class);
    verify(commons).executeTurnAndDeleteMessage(any(), any(), captor.capture(), any(), any());
    SubjectTurn turn = captor.getValue();
    assertThat(turn.getActions()).isNotEmpty();
    assertThat(turn.getActions().get(0).getOperationType()).isEqualTo(OperationType.MOVE);
  }

}