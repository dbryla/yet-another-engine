package dbryla.game.yetanotherengine.session;

import dbryla.game.yetanotherengine.domain.equipment.Weapon;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.SubjectProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedList;
import java.util.List;

@ToString
@Getter
public class FightSession {

  private final SubjectProperties subjectProperties;
  @Setter
  private boolean spellCasting = false;
  @Setter
  private boolean isMoving = false;
  @Setter
  private boolean isStandingUp = false;
  private String playerName;
  private Weapon weapon;
  @Setter
  private Spell spell;
  private List<String> targets = new LinkedList<>();

  public FightSession(String playerName, SubjectProperties subjectProperties) {
    this.playerName = playerName;
    this.subjectProperties = subjectProperties;
  }

  public boolean areAllTargetsAcquired() {
    return spell.getMaximumNumberOfTargets() == targets.size();
  }

  public void cleanUpCallbackData() {
    setMoving(false);
    setStandingUp(false);
    targets.clear();
  }

  public void addTarget(String target) {
    targets.add(target);
  }

  public void setWeapon(String weapon) {
    this.weapon = Weapon.valueOf(weapon);
  }

}
