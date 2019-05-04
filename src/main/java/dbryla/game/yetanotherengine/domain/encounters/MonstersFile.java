package dbryla.game.yetanotherengine.domain.encounters;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MonstersFile {
  private List<MonsterDefinition> monsters;
}
