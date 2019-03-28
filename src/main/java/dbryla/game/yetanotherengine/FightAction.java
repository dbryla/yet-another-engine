package dbryla.game.yetanotherengine;

import java.util.List;
import java.util.Set;

public class FightAction implements Action {

  private final String sourceName;
  private final List<String> targetNames;
  private final Operation operation;

  public FightAction(String sourceName, List<String> targetNames, Operation operation) {
    this.sourceName = sourceName;
    this.targetNames = targetNames;
    this.operation = operation;
  }

  @Override
  public String getSourceName() {
    return sourceName;
  }

  @Override
  public List<String> getTargetNames() {
    return targetNames;
  }

  @Override
  public Set<Subject> invoke(Subject source, Subject... targets) {
    return null;
  }
}
