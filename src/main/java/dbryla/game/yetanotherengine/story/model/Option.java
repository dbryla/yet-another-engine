package dbryla.game.yetanotherengine.story.model;

import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@ToString
public class Option {

  private String name;
  private OptionType type;
  private int durationInMinutes;
  private List<Outcome> outcome;
  private boolean repeatable;
  private Map<String, Object> properties;
}
