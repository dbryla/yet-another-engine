package dbryla.game.yetanotherengine.domain.game;

import dbryla.game.yetanotherengine.domain.ai.ArtificialIntelligence;
import dbryla.game.yetanotherengine.domain.game.state.StateMachineFactory;
import dbryla.game.yetanotherengine.domain.game.state.storage.SubjectStorage;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.SubjectProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static dbryla.game.yetanotherengine.domain.subject.Affiliation.ENEMIES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameTest {

  @InjectMocks
  private Game game;

  @Mock
  private SubjectStorage subjectStorage;

  @Mock
  private StateMachineFactory stateMachineFactory;

  @Mock
  private ArtificialIntelligence artificialIntelligence;

  @Test
  void shouldSaveCreatedCharacter() {
    Subject subject = mock(Subject.class);

    game.createPlayerCharacter(subject);

    verify(subjectStorage).save(any(), eq(subject));
  }

  @Test
  void shouldSaveCreatedEnemies() {
    game.createNonPlayableCharacters((List.of(mock(Subject.class))));

    verify(subjectStorage, atLeastOnce()).save(any(), any());
  }

  @Test
  void shouldGetAllEnemies() {
    Subject target = mock(Subject.class);
    when(target.getName()).thenReturn("enemy");
    when(target.getAffiliation()).thenReturn(ENEMIES);
    when(target.isAlive()).thenReturn(true);
    when(subjectStorage.findAll(any())).thenReturn(List.of(target));

    List<String> allEnemies = game.getAllAliveEnemyNames();

    assertThat(allEnemies).contains("enemy");
  }
}