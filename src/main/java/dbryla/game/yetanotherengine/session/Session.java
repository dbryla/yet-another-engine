package dbryla.game.yetanotherengine.session;

import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.telegram.Communicate;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

import static dbryla.game.yetanotherengine.telegram.BuildingFactory.ABILITIES;
import static dbryla.game.yetanotherengine.telegram.FightFactory.SPELL;
import static dbryla.game.yetanotherengine.telegram.FightFactory.TARGET;

@ToString
public class Session {

  @Getter
  private final Map<String, Object> data = new HashMap<>();
  @Getter
  private final String playerName;
  private final List<Communicate> communicates;
  @Getter
  @Setter
  private Subject subject;
  @Getter
  private List<Integer> abilityScores;
  @Getter
  @Setter
  private boolean spellCasting = false;
  @Getter
  @Setter
  private boolean isMoving = false;

  public Session(String playerName, List<Communicate> communicates, List<Integer> abilityScores) {
    this.playerName = playerName;
    this.communicates = communicates;
    this.abilityScores = abilityScores;
  }

  public Session(String playerName, Subject subject) {
    this.playerName = playerName;
    this.subject = subject;
    this.communicates = List.of();
  }

  public void update(String key, String value) {
    if (key.equals(ABILITIES)) {
      data.putIfAbsent(ABILITIES, new LinkedList<>());
      ((LinkedList) data.get(ABILITIES)).add(value);
      return;
    }
    if (key.equals(TARGET)) {
      data.putIfAbsent(TARGET, new LinkedList<>());
      ((LinkedList) data.get(TARGET)).add(value);
      return;
    }
    data.put(key, value);
  }

  public Optional<Communicate> getNextBuildingCommunicate() {
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

  public boolean areAllTargetsAcquired() {
    return Spell.valueOf((String) data.get(SPELL)).getMaximumNumberOfTargets() == ((List) data.get(TARGET)).size();
  }

  public List<String> getTargets() {
    return (List<String>) data.get(TARGET);
  }

  public void clearTargets() {
    List list = (List) data.get(TARGET);
    if (list != null) {
      list.clear();
    }
  }

}
