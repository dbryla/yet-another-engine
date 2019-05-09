package dbryla.game.yetanotherengine.session;

import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class FightSession {

  /**
   * used for regular game
   */
  @Getter
  @Setter
  private Subject subject;
  @Getter
  @Setter
  private boolean spellCasting = false;
  @Getter
  @Setter
  private boolean isMoving = false;
  @Getter
  @Setter
  private boolean isStandingUp = false;
  @Getter
  private String playerName;
  @Getter
  private Weapon weapon;
  @Getter
  private Spell spell;
  @Getter
  private List<String> targets = new LinkedList<>();

  public FightSession(String playerName, Subject subject) {
    this.playerName = playerName;
    this.subject = subject;
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

  public void setSpell(String spell) {
    this.spell = Spell.valueOf(spell);
  }

  public void setWeapon(String weapon) {
    this.weapon = Weapon.valueOf(weapon);
  }

}
