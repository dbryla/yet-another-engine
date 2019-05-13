package dbryla.game.yetanotherengine.story.state;

import dbryla.game.yetanotherengine.domain.subject.Abilities;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.story.model.Story;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("story")
@SpringBootTest
class InMemoryStoryStateMachineITest {

  @Autowired
  private List<Story> stories;

  @Autowired
  private StoryStateMachine storyStateMachine;

  @Test
  void shouldProceedWithStory() {
    Story story = stories.get(0);
    Subject subject = mock(Subject.class);
    when(subject.getAbilities()).thenReturn(new Abilities(10, 10, 10, 10, 10, 10));

    storyStateMachine.start(story);

    storyStateMachine.pickOption(subject, story.getName(), 0);
  }
}