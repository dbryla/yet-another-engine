package dbryla.game.yetanotherengine.story.state;

import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.story.model.Story;

public interface StoryStateMachine {
  void start(Story story);

  void pickOption(Subject subject, String storyName, int optionNumber);
}
