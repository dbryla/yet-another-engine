package dbryla.game.yetanotherengine.cli;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsoleConfig {

  @Bean
  public BufferedReader bufferedReader() {
    return new BufferedReader(new InputStreamReader(System.in));
  }

}
