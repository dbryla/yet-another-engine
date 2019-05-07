package dbryla.game.yetanotherengine.session;

import static dbryla.game.yetanotherengine.telegram.BuildingFactory.ABILITIES;
import static dbryla.game.yetanotherengine.telegram.BuildingFactory.EXTRA_ABILITIES;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Session {

  @Getter
  private final Map<String, Object> genericData = new HashMap<>();
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
  @Getter
  @Setter
  private boolean isStandingUp = false;
  @Getter(AccessLevel.PACKAGE)
  private SessionData data;

  public Session(String playerName, List<Communicate> communicates, List<Integer> abilityScores) {
    this.data = SessionData.builder().playerName(playerName).communicates(communicates).build();
    this.abilityScores = abilityScores;
  }

  public Session(String playerName, Subject subject) {
    this.data = SessionData.builder().playerName(playerName).build();
    this.subject = subject;
  }

  public Session(SessionData data) {
    this.data = data;
  }

  public static Session of(SessionData data) {
    return new Session(data);
  }

  public void update(String key, String value) {
    if (key.equals(ABILITIES)) {
      genericData.putIfAbsent(ABILITIES, new LinkedList<>());
      listOf(ABILITIES).add(value);
      return;
    }
    if (key.equals(EXTRA_ABILITIES)) {
      genericData.putIfAbsent(EXTRA_ABILITIES, new LinkedList<>());
      listOf(EXTRA_ABILITIES).add(value);
      return;
    }
    if (key.equals(TARGETS)) {
      genericData.putIfAbsent(TARGETS, new LinkedList<>());
      listOf(TARGETS).add(value);
      return;
    }
    if (key.equals(WEAPONS)) {
      genericData.putIfAbsent(WEAPONS, new LinkedList<>());
      listOf(WEAPONS).add(value);
      return;
    }
    genericData.put(key, value);
  }

  @SuppressWarnings("unchecked")
  public List<String> listOf(String abilities) {
    return (List<String>) genericData.get(abilities);
  }

  public Optional<Communicate> getNextCommunicate() {
    if (data.getCommunicates().isEmpty()) {
      return Optional.empty();
    }
    return Optional.ofNullable(data.getCommunicates().remove(0));
  }

  public void addNextCommunicate(Communicate communicate) {
    data.getCommunicates().add(0, communicate);
  }

  public void addLastCommunicate(Communicate communicate) {
    data.getCommunicates().add(communicate);
  }

  public boolean areAllTargetsAcquired() {
    return Spell.valueOf((String) genericData.get(SPELL)).getMaximumNumberOfTargets() == (listOf(TARGETS)).size();
  }

  public List<String> getTargets() {
    return listOf(TARGETS);
  }

  private void clearTargets() {
    List list = listOf(TARGETS);
    if (list != null) {
      list.clear();
    }
  }

  public void cleanUpCallbackData() {
    setMoving(false);
    setStandingUp(false);
    clearTargets();
  }

  public Weapon getWeapon() {
    return Weapon.valueOf((String) genericData.get(WEAPON));
  }

  public String getPlayerName() {
    return data.getPlayerName();
  }
}
