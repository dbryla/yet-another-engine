package dbryla.game.yetanotherengine.domain.encounters;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class EncountersConfig {

  @Bean
  public Map<Double, List<MonsterDefinition>> monsters(ObjectMapper objectMapper) throws IOException {
    MonstersFile monstersFile = objectMapper.readValue(ResourceUtils.getFile("classpath:monsters.json"), MonstersFile.class);
    return monstersFile.getMonsters().stream().collect(Collectors.groupingBy(MonsterDefinition::getChallengeRating));
  }

}
