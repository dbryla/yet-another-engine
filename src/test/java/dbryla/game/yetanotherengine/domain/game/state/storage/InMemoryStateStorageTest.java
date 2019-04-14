package dbryla.game.yetanotherengine.domain.game.state.storage;

import dbryla.game.yetanotherengine.domain.subject.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InMemoryStateStorageTest {

  private final Long gameId = 123L;
  private final String subjectName = "name";

  private final InMemoryStateStorage inMemoryStateStorage = new InMemoryStateStorage();

  @Mock
  private Subject subject;

  @BeforeEach
  void setUp() {
    when(subject.getName()).thenReturn(subjectName);
    inMemoryStateStorage.save(gameId, subject);
  }

  @Test
  void shouldReturnSavedSubject() {
    Optional<Subject> foundObject = inMemoryStateStorage.findByIdAndName(gameId, subjectName);
    assertThat(foundObject).isPresent();
    assertThat(foundObject.get()).isEqualTo(subject);
  }

  @Test
  void shouldReturnListWithSavedObject() {
    assertThat(inMemoryStateStorage.findAll(gameId)).contains(subject);
  }

  @Test
  void shouldRemoveAllDataForGame() {
    inMemoryStateStorage.removeAll(gameId);

    assertThat(inMemoryStateStorage.findAll(gameId)).doesNotContain(subject);
  }
}