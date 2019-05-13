package dbryla.game.yetanotherengine.story.state;

import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.story.challenge.ChallengeFactory;
import dbryla.game.yetanotherengine.story.model.Option;
import dbryla.game.yetanotherengine.story.model.Outcome;
import dbryla.game.yetanotherengine.story.model.Story;
import dbryla.game.yetanotherengine.story.model.StoryEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Profile("story")
@Slf4j
public class InMemoryStoryStateMachine implements StoryStateMachine {

  private final Map<String, StoryState> stories = new HashMap<>();
  private final ChallengeFactory challengeFactory;

  public InMemoryStoryStateMachine(ChallengeFactory challengeFactory) {
    this.challengeFactory = challengeFactory;
  }

  @Override
  public void start(Story story) {
    log.info("Starting {}", story.getName());
    log.info(story.getQuestText());
    nextEvent(story, 0);
  }

  private void nextEvent(Story story, int eventIndex) {
    StoryEvent storyEvent = story.getEvents().get(eventIndex);
    log.info("Event no. {}: {}", eventIndex, storyEvent.getName());
    log.info(storyEvent.getText());
    log.info("Options: ");
    storyEvent.getOptions().forEach(option -> log.info(option.getName()));
    stories.put(story.getName(), new StoryState(story, eventIndex));
  }

  @Override
  public void pickOption(Subject subject, String storyName, int optionNumber) {
    log.info("Running {}", storyName);
    StoryState state = stories.get(storyName);
    StoryEvent storyEvent = state.getStory().getEvents().get(state.getEventIndex());
    Option option = storyEvent.getOptions().get(optionNumber);
    log.info(option.getName());
    List<Outcome> outcomes = challengeFactory.createChallenge(option.getType()).invoke(subject, option.getOutcome());
    outcomes.forEach(outcome -> log.info(outcome.getText()));
    nextEvent(state.getStory(), state.getEventIndex() + 1);
  }
}
