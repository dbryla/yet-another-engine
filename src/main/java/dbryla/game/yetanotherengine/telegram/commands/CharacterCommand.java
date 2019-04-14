package dbryla.game.yetanotherengine.telegram.commands;

import dbryla.game.yetanotherengine.db.CharacterRepository;
import dbryla.game.yetanotherengine.domain.subject.SubjectMapper;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.session.Session;
import dbryla.game.yetanotherengine.telegram.SessionFactory;
import dbryla.game.yetanotherengine.telegram.TelegramClient;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static dbryla.game.yetanotherengine.telegram.TelegramHelpers.getCharacterName;
import static dbryla.game.yetanotherengine.telegram.TelegramHelpers.getSessionId;

@Component
@AllArgsConstructor
@Profile("tg")
public class CharacterCommand {
  private final SessionFactory sessionFactory;
  private final CharacterRepository characterRepository;
  private final SubjectMapper subjectMapper;
  private final TelegramClient telegramClient;

  public void execute(Update update) {
    Message message = update.getMessage();
    Long chatId = update.getMessage().getChatId();
    String playerName = getCharacterName(message.getFrom());
    String sessionId = getSessionId(message, message.getFrom());
    Session existingSession = sessionFactory.getSession(sessionId);
    if (existingSession == null) {
      characterRepository.findByName(playerName)
          .map(subjectMapper::fromCharacter)
          .map(subject -> sessionFactory.createSession(sessionId, playerName, subject))
          .ifPresentOrElse(
              session -> telegramClient.sendTextMessage(chatId, playerName + ": Your character.\n" + session.getSubject()),
              () -> telegramClient.sendTextMessage(chatId, playerName + ": No existing character."));
    } else {
      Subject subject = existingSession.getSubject();
      telegramClient.sendTextMessage(chatId, playerName + ": Your character.\n" + subject);
    }
  }
}
