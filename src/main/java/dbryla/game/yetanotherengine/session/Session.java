package dbryla.game.yetanotherengine.session;

import static dbryla.game.yetanotherengine.telegram.CommunicateFactory.ABILITIES;

import dbryla.game.yetanotherengine.telegram.Communicate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.ToString;

@ToString
public class Session {

  @Getter
  private final Map<String, Object> data = new HashMap<>();
  @Getter
  private final String playerName;
  private final List<Communicate> communicates;
  @Getter
  private final Integer originalMessageId;
  @Getter
  private final List<Integer> abilityScores;

  public Session(String playerName, Integer originalMessageId,
                 List<Communicate> communicates, List<Integer> abilityScores) {
    this.playerName = playerName;
    this.originalMessageId = originalMessageId;
    this.communicates = communicates;
    this.abilityScores = abilityScores;
  }

  public void update(String key, String value) {
    if (key.equals(ABILITIES)) {
      data.putIfAbsent(ABILITIES, new LinkedList<>());
      ((LinkedList) data.get(ABILITIES)).add(value);
    } else {
      data.put(key, value);
    }
  }

  public Optional<Communicate> getNextCommunicate() {
    if (communicates.isEmpty()) {
      return Optional.empty();
    }
    return Optional.ofNullable(communicates.remove(0));
  }

  public void addNextCommunicate(Communicate communicate) {
    communicates.add(0, communicate);
  }

  public void addLastCommunicate(Communicate communicate) {
    communicates.add(communicate);
  }
}
