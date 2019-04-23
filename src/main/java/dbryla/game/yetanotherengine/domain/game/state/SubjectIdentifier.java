package dbryla.game.yetanotherengine.domain.game.state;

import dbryla.game.yetanotherengine.domain.subject.Affiliation;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class SubjectIdentifier {

  private final String name;
  private final Affiliation affiliation;

}
