package dbryla.game.yetanotherengine;

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
