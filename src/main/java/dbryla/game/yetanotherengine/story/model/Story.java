package dbryla.game.yetanotherengine.story.model;

import lombok.*;

import java.util.List;

@Getter
@ToString
public class Story {

  private String name;
  private int requiredLevel;
  private String questText;
  private List<StoryEvent> events;

}
