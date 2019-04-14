package dbryla.game.yetanotherengine.telegram;

import org.springframework.context.annotation.Configuration;

@Configuration
class TelegramConfig {

  String getToken() {
    return System.getenv("TELEGRAM_TOKEN");
  }
}
