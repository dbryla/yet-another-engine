package dbryla.game.yetanotherengine.domain.game;

import dbryla.game.yetanotherengine.domain.ai.ArtificialIntelligence;
import dbryla.game.yetanotherengine.domain.game.state.StateMachineFactory;
import dbryla.game.yetanotherengine.domain.game.state.storage.StateStorage;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static dbryla.game.yetanotherengine.domain.subject.Affiliation.ENEMIES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameTest {

  @InjectMocks
  private Game game;

  @Mock
  private StateStorage stateStorage;

  @Mock
  private StateMachineFactory stateMachineFactory;

  @Mock
  private ArtificialIntelligence artificialIntelligence;

  @Test
  void shouldSaveCreatedCharacter() {
    Subject subject = mock(Subject.class);

    game.createPlayerCharacter(subject);

    verify(stateStorage).save(any(), eq(subject));
  }

  @Test
  void shouldSaveCreatedEnemies() {
    game.createNonPlayableCharacters((List.of(mock(Subject.class))));

    verify(stateStorage, atLeastOnce()).save(any(), any());
  }

  @Test
  void shouldGetAllEnemies() {
    Subject target = mock(Subject.class);
    when(target.getName()).thenReturn("enemy");
    when(target.getAffiliation()).thenReturn(ENEMIES);
    when(stateStorage.findAll(any())).thenReturn(List.of(target));

    List<String> allEnemies = game.getAllAliveEnemyNames();

    assertThat(allEnemies).contains("enemy");
  }
}