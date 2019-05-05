package dbryla.game.yetanotherengine.domain.game;

import dbryla.game.yetanotherengine.domain.operations.ActionData;
import dbryla.game.yetanotherengine.domain.operations.OperationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class Action {

  private final String sourceName;
  private final List<String> targetNames;
  private final OperationType operationType;
  private final ActionData actionData;

  public Action(String sourceName, String targetName, OperationType operationType, ActionData actionData) {
    this.sourceName = sourceName;
    this.targetNames = List.of(targetName);
    this.operationType = operationType;
    this.actionData = actionData;
  }

  public Action(String sourceName, OperationType operationType, ActionData actionData) {
    this.sourceName = sourceName;
    this.targetNames = List.of();
    this.operationType = operationType;
    this.actionData = actionData;
  }

  public Action(String sourceName, OperationType operationType) {
    this.sourceName = sourceName;
    this.targetNames = List.of();
    this.operationType = operationType;
    this.actionData = null;
  }

}
