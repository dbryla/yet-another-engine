package dbryla.game.yetanotherengine;

import java.util.Optional;

public interface StateStorage {
  Optional<Subject> findByName(String name);

  void save(Subject subject);

  Iterable<Subject> findAll();
}
