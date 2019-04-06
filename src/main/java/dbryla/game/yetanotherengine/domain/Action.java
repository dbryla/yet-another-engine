package dbryla.game.yetanotherengine.domain;

import dbryla.game.yetanotherengine.domain.operations.Operation;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class Action {

  private final String sourceName;
  private final List<String> targetNames;
  private final Operation operation;
  private final Instrument instrument;

  public Action(String sourceName, String targetName, Operation operation, Instrument instrument) {
    this.sourceName = sourceName;
    this.targetNames = List.of(targetName);
    this.operation = operation;
    this.instrument = instrument;
  }

}
