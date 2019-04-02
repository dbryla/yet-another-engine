package dbryla.game.yetanotherengine.domain.state.storage;

import dbryla.game.yetanotherengine.domain.subjects.Subject;
import java.util.Optional;

public interface StateStorage {
  Optional<Subject> findByName(String name);

  void save(Subject subject);

  Iterable<Subject> findAll();
}
