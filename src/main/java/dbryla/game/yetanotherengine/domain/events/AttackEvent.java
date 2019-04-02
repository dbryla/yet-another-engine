package dbryla.game.yetanotherengine.domain.events;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class AttackEvent implements Event {

  private final String message;

  public AttackEvent(String message) {
    this.message = message;
  }

  public static AttackEvent success(String attacker, String target, boolean isTargetTerminated) {
    String message = attacker + " hits " + target + ".";
    if (isTargetTerminated) {
      message += " " + target + " drops on the ground.";
    }
    return new AttackEvent(message);
  }

  public static AttackEvent fail(String attacker, String target) {
    String message = attacker + " misses attack on " + target + ".";
    return new AttackEvent(message);
  }

  @Override
  public String toString() {
    return message;
  }
}
