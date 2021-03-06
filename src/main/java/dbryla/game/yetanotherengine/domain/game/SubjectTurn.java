package dbryla.game.yetanotherengine.domain.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@Getter
public class SubjectTurn {
  private final List<Action> actions;
  private final String ownerName;

  public SubjectTurn(String ownerName) {
    this.ownerName = ownerName;
    this.actions = new LinkedList<>();
  }

  public static SubjectTurn of(Action action) {
    SubjectTurn subjectTurn = new SubjectTurn(action.getSourceName());
    subjectTurn.add(action);
    return subjectTurn;
  }

  public SubjectTurn add(Action action) {
    actions.add(action);
    return this;
  }
}
