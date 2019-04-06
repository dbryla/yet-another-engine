package dbryla.game.yetanotherengine.state.storage;

import dbryla.game.yetanotherengine.domain.state.storage.InMemoryStateStorage;
import dbryla.game.yetanotherengine.domain.subjects.classes.Subject;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InMemoryStateStorageTest {

  @Test
  void shouldReturnSavedSubject() {
    Subject subject = mock(Subject.class);
    String subjectName = "name";
    when(subject.getName()).thenReturn(subjectName);
    InMemoryStateStorage inMemoryStateStorage = new InMemoryStateStorage();

    inMemoryStateStorage.save(subject);

    Optional<Subject> foundObject = inMemoryStateStorage.findByName(subjectName);
    assertThat(foundObject).isPresent();
    assertThat(foundObject.get()).isEqualTo(subject);
  }

  @Test
  void shouldReturnListWithSavedObject() {
    Subject subject = mock(Subject.class);
    String subjectName = "name";
    when(subject.getName()).thenReturn(subjectName);
    InMemoryStateStorage inMemoryStateStorage = new InMemoryStateStorage();

    inMemoryStateStorage.save(subject);

    assertThat(inMemoryStateStorage.findAll()).contains(subject);
  }
}