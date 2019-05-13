package dbryla.game.yetanotherengine.story.challenge;

import dbryla.game.yetanotherengine.story.model.OptionType;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("story")
@AllArgsConstructor
public class ChallengeFactory {

  public Challenge createChallenge(OptionType type) {
    if (type == OptionType.ASK_AROUND) {
      return new AskAroundChallenge();
    }
    throw new IllegalArgumentException("Not supported type: " + type);
  }
}
