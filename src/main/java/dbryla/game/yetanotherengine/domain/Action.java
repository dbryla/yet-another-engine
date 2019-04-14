package dbryla.game.yetanotherengine.domain;

import dbryla.game.yetanotherengine.domain.operations.Instrument;
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
  private final Instrument instrument;

  public Action(String sourceName, String targetName, OperationType operationType, Instrument instrument) {
    this.sourceName = sourceName;
    this.targetNames = List.of(targetName);
    this.operationType = operationType;
    this.instrument = instrument;
  }

}
