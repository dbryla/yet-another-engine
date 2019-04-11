package dbryla.game.yetanotherengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
@EnableAsync
public class YetAnotherEngineApplication {

	public static void main(String[] args) {
		ApiContextInitializer.init();
		SpringApplication.run(YetAnotherEngineApplication.class, args);
	}

}
