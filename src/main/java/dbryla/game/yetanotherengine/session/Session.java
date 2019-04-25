package dbryla.game.yetanotherengine.session;

import static dbryla.game.yetanotherengine.telegram.BuildingFactory.ABILITIES;
import static dbryla.game.yetanotherengine.telegram.BuildingFactory.WEAPONS;
import static dbryla.game.yetanotherengine.telegram.FightFactory.SPELL;
import static dbryla.game.yetanotherengine.telegram.FightFactory.TARGETS;
import static dbryla.game.yetanotherengine.telegram.FightFactory.WEAPON;

import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import dbryla.game.yetanotherengine.telegram.Communicate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
    this.communicates = new LinkedList<>();
  }

  public void update(String key, String value) {
    if (key.equals(ABILITIES)) {
      data.putIfAbsent(ABILITIES, new LinkedList<>());
      ((List) data.get(ABILITIES)).add(value);
      return;
    }
    if (key.equals(TARGETS)) {
      data.putIfAbsent(TARGETS, new LinkedList<>());
      ((List) data.get(TARGETS)).add(value);
      return;
    }
    if (key.equals(WEAPONS)) {
      data.putIfAbsent(WEAPONS, new LinkedList<>());
      ((List) data.get(WEAPONS)).add(value);
      return;
    }
    data.put(key, value);
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

  public boolean areAllTargetsAcquired() {
    return Spell.valueOf((String) data.get(SPELL)).getMaximumNumberOfTargets() == ((List) data.get(TARGETS)).size();
  }

  public List<String> getTargets() {
    return (List<String>) data.get(TARGETS);
  }

  private void clearTargets() {
    List list = (List) data.get(TARGETS);
    if (list != null) {
      list.clear();
    }
  }

  public void cleanUpCallbackData() {
    setMoving(false);
    clearTargets();
  }

  public Weapon getWeapon() {
    return Weapon.valueOf((String) data.get(WEAPON));
  }
}
