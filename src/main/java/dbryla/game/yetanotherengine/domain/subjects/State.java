package dbryla.game.yetanotherengine.domain.subjects;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum State {
  NORMAL(" looks great."),
  LIGHTLY_WOUNDED(" is lightly wounded."),
  WOUNDED(" is wounded."),
  HEAVILY_WOUNDED(" is heavily wounded."),
  DEATHS_DOOR(" is at death's door."),
  TERMINATED(" drops dead.");

  private final String message;

  public static String getMessageFor(Subject target) {
    return " " + target.getName() + target.getSubjectState().message;
  }

  public boolean needsHealing() {
    return HEAVILY_WOUNDED.equals(this) || DEATHS_DOOR.equals(this);
  }
}
