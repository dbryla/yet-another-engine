package dbryla.game.yetanotherengine.domain.game;

import static dbryla.game.yetanotherengine.domain.game.GameOptions.ENEMIES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dbryla.game.yetanotherengine.domain.ai.ArtificialIntelligence;
import dbryla.game.yetanotherengine.domain.game.state.StateMachineFactory;
import dbryla.game.yetanotherengine.domain.game.state.storage.StateStorage;

import java.util.List;

import dbryla.game.yetanotherengine.domain.subject.Subject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    game.createCharacter(subject);

    verify(stateStorage).save(any(), eq(subject));
  }

  @Test
  void shouldSaveCreatedEnemies() {
    game.createEnemies((List.of(mock(Subject.class))));

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