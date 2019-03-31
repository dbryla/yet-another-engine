package dbryla.game.yetanotherengine;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
