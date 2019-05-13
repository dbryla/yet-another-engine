package dbryla.game.yetanotherengine.story.state;

import dbryla.game.yetanotherengine.story.model.Story;
import lombok.Data;

@Data
public class StoryState {
  private final Story story;
  private final int eventIndex;

}
