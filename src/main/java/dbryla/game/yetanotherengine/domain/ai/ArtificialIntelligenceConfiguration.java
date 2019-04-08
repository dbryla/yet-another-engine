package dbryla.game.yetanotherengine.domain.ai;

import dbryla.game.yetanotherengine.domain.subjects.Subject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ArtificialIntelligenceConfiguration {

  private final Subject subject;
  private String acquiredTarget;

}
