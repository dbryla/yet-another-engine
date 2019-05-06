package dbryla.game.yetanotherengine.story.model;

import lombok.*;

import java.util.List;

@ToString
@Getter
public class StoryEvent {

  private String name;
  private String text;
  private List<Option> options;
  private List<Option> bonusOptions;
}
