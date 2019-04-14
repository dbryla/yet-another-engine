package dbryla.game.yetanotherengine.domain.game.state.storage;

import java.util.*;

import dbryla.game.yetanotherengine.domain.subject.Subject;
import org.springframework.stereotype.Component;

@Component
public class InMemoryStateStorage implements StateStorage {

  private final Map<Long, Map<String, Subject>> storage = new HashMap<>();

  @Override
  public Optional<Subject> findByIdAndName(Long gameId, String name) {
    if (!storage.containsKey(gameId)) {
      return Optional.empty();
    }
    return Optional.ofNullable(storage.get(gameId).get(name));
  }

  @Override
  public void save(Long gameId, Subject subject) {
    storage.putIfAbsent(gameId, new HashMap<>());
    storage.get(gameId).put(subject.getName(), subject);
  }

  @Override
  public List<Subject> findAll(Long gameId) {
    return new ArrayList<>(storage.getOrDefault(gameId, Map.of()).values());
  }

  @Override
  public void removeAll(Long gameId) {
    storage.remove(gameId);
  }
}
