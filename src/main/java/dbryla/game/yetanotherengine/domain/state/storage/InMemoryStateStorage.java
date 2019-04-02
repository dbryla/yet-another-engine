package dbryla.game.yetanotherengine.domain.state.storage;

import dbryla.game.yetanotherengine.domain.subjects.Subject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class InMemoryStateStorage implements StateStorage {

  private final Map<String, Subject> storage = new HashMap<>();

  @Override
  public Optional<Subject> findByName(String name) {
    return Optional.ofNullable(storage.get(name));
  }

  @Override
  public void save(Subject subject) {
    storage.put(subject.getName(), subject);
  }

  @Override
  public Iterable<Subject> findAll() {
    return storage.values();
  }
}
