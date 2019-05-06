package dbryla.game.yetanotherengine.story.model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Outcome {

  private int dc;
  private String text;
  private String bonusOption;
  private String nextEvent;
  private boolean makeNoise;
  private String effect;
}
