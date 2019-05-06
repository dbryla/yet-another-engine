package dbryla.game.yetanotherengine.story;

import dbryla.game.yetanotherengine.story.model.Story;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("story")
class StoryConfigITest {

  @Autowired
  private List<Story> stories;

  @Test
  void shouldLoadStoryFromJson() {
    assertThat(stories).isNotEmpty();
  }
}