package dbryla.game.yetanotherengine.story.challenge;

import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.story.model.Outcome;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Profile("story")
public class AskAroundChallenge implements Challenge {

  @Override
  public List<Outcome> invoke(Subject subject, List<Outcome> possibleOutcomes) {
    int abilityCheck = 10 + subject.getAbilities().getCharismaModifier();
    return possibleOutcomes.stream().filter(outcome -> outcome.getDc() <= abilityCheck).collect(Collectors.toList());
  }
}
