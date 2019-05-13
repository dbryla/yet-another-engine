package dbryla.game.yetanotherengine.story.challenge;

import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.story.model.Outcome;

import java.util.List;

public interface Challenge {
  List<Outcome> invoke(Subject subject, List<Outcome> possibleOutcomes);
}
