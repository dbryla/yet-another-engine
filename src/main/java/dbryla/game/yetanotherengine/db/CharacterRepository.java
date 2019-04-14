package dbryla.game.yetanotherengine.db;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CharacterRepository extends MongoRepository<PlayerCharacter, String> {
  Optional<PlayerCharacter> findByName(String name);
}
