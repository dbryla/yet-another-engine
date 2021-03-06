package dbryla.game.yetanotherengine.domain.game.state.storage;

import dbryla.game.yetanotherengine.domain.subject.Subject;
import java.util.List;
import java.util.Optional;

public interface SubjectStorage {

  Optional<Subject> findByIdAndName(Long gameId, String name);

  void save(Long gameId, Subject subject);

  List<Subject> findAll(Long gameId);

  void deleteAll(Long gameId);
}
