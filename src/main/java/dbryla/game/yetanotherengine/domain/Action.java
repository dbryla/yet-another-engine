package dbryla.game.yetanotherengine.domain;

import dbryla.game.yetanotherengine.domain.operations.Operation;
import java.util.List;

public class Action {

  private final String sourceName;
  private final List<String> targetNames;
  private final Operation operation;

  public Action(String sourceName, List<String> targetNames, Operation operation) {
    this.sourceName = sourceName;
    this.targetNames = targetNames;
    this.operation = operation;
  }

  public Action(String sourceName, String targetName, Operation operation) {
    this.sourceName = sourceName;
    this.targetNames = List.of(targetName);
    this.operation = operation;
  }

  public String getSourceName() {
    return sourceName;
  }

  public List<String> getTargetNames() {
    return targetNames;
  }

  public Operation getOperation() {
    return operation;
  }

}