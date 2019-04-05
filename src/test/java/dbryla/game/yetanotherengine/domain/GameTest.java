package dbryla.game.yetanotherengine.domain;

import static dbryla.game.yetanotherengine.domain.GameOptions.ENEMIES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dbryla.game.yetanotherengine.InputProvider;
import dbryla.game.yetanotherengine.domain.ai.ArtificialIntelligence;
import dbryla.game.yetanotherengine.domain.state.StateMachineFactory;
import dbryla.game.yetanotherengine.domain.state.storage.StateStorage;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import java.util.List;
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

  @Mock
  private InputProvider inputProvider;

  @Test
  void shouldSaveCreatedCharacter() {
    Subject subject = mock(Subject.class);

    game.createCharacter(subject);

    verify(stateStorage).save(eq(subject));
  }

  @Test
  void shouldSaveCreatedEnemies() {
    game.createEnemies();

    verify(stateStorage, atLeastOnce()).save(any());
  }

  @Test
  void shouldGetAllEnemies() {
    Subject target = mock(Subject.class);
    when(target.getName()).thenReturn("enemy");
    when(target.getAffiliation()).thenReturn(ENEMIES);
    when(stateStorage.findAll()).thenReturn(List.of(target));

    List<String> allEnemies = game.getAllAliveEnemies();

    assertThat(allEnemies).contains("enemy");
  }
}