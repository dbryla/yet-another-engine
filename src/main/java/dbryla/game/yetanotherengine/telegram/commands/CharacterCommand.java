package dbryla.game.yetanotherengine.telegram.commands;

import dbryla.game.yetanotherengine.db.CharacterRepository;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.SubjectFactory;
import dbryla.game.yetanotherengine.session.FightSession;
import dbryla.game.yetanotherengine.telegram.Commons;
import dbryla.game.yetanotherengine.telegram.SessionFactory;
import dbryla.game.yetanotherengine.telegram.TelegramClient;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@AllArgsConstructor
@Profile("tg")
public class CharacterCommand {

  private final SessionFactory sessionFactory;
  private final CharacterRepository characterRepository;
  private final SubjectFactory subjectFactory;
  private final TelegramClient telegramClient;
  private final Commons commons;

  public void execute(Update update) {
    Message message = update.getMessage();
    Long chatId = update.getMessage().getChatId();
    String playerName = commons.getCharacterName(message.getFrom());
    String sessionId = commons.getSessionId(message, message.getFrom());
    FightSession existingSession = sessionFactory.getFightSession(sessionId);
    if (existingSession == null) {
      characterRepository.findByName(playerName)
          .map(subjectFactory::fromCharacter)
          .map(subject -> sessionFactory.createFightSession(sessionId, playerName, subject))
          .ifPresentOrElse(
              session -> telegramClient.sendTextMessage(chatId, playerName + ": Your character.\n" + session.getSubjectProperties()),
              () -> telegramClient.sendTextMessage(chatId, playerName + ": No existing character."));
    } else {
      telegramClient.sendTextMessage(chatId, playerName + ": Your character.\n" + existingSession.getSubjectProperties());
    }
  }
}
