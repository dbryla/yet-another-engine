package dbryla.game.yetanotherengine.domain.subject;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum HealthState {
  NORMAL(" looks great."),
  LIGHTLY_WOUNDED(" is lightly wounded."),
  WOUNDED(" is wounded."),
  HEAVILY_WOUNDED(" is heavily wounded."),
  DEATHS_DOOR(" is at death's door."),
  TERMINATED(" is dead.");

  private final String message;

  public static String getMessageFor(State subject) {
    return " " + subject.getSubjectName() + subject.getHealthState().message;
  }

  public boolean needsHealing() {
    return HEAVILY_WOUNDED.equals(this) || DEATHS_DOOR.equals(this);
  }

  @Override
  public String toString() {
    return this.message;
  }
}
