package dbryla.game.yetanotherengine.story;

import com.fasterxml.jackson.databind.ObjectMapper;
import dbryla.game.yetanotherengine.story.model.Story;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.util.List;

@Profile("story")
@Configuration
public class StoryConfig {

  @Bean
  public List<Story> stories(ObjectMapper objectMapper) throws IOException {
    Story story = objectMapper.readValue(ResourceUtils.getFile("classpath:story.json"), Story.class);
    return List.of(story);
  }
}
