package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.events.EventFactory;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static dbryla.game.yetanotherengine.domain.battleground.Position.PLAYERS_BACK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MoveOperationTest {

  @InjectMocks
  private MoveOperation moveOperation;

  @Mock
  private EventFactory eventFactory;

  @Test
  void shouldMoveSubjectToGivenPosition() throws UnsupportedGameOperationException {
    Subject subject = mock(Subject.class);
    Subject changedSubject = mock(Subject.class);
    when(changedSubject.getPosition()).thenReturn(PLAYERS_BACK);
    when(subject.of(eq(PLAYERS_BACK))).thenReturn(changedSubject);

    OperationResult result = moveOperation.invoke(subject, new ActionData(PLAYERS_BACK));

    assertThat(result.getChangedSubjects()).isNotEmpty();
    assertThat(result.getChangedSubjects()).extracting("position").contains(PLAYERS_BACK);
  }
}