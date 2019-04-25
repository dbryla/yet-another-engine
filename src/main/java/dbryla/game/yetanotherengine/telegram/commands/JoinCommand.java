package dbryla.game.yetanotherengine.telegram.commands;

import dbryla.game.yetanotherengine.db.CharacterRepository;
import dbryla.game.yetanotherengine.db.PlayerCharacter;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.SubjectFactory;
import dbryla.game.yetanotherengine.session.Session;
import dbryla.game.yetanotherengine.telegram.Commons;
import dbryla.game.yetanotherengine.telegram.Communicate;
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
public class JoinCommand {

  private final SessionFactory sessionFactory;
  private final TelegramClient telegramClient;
  private final CharacterRepository characterRepository;
  private final SubjectFactory subjectFactory;
  private final Commons commons;

  public void execute(Update update) {
    Message message = update.getMessage();
    String playerName = commons.getCharacterName(message.getFrom());
    String sessionId = commons.getSessionId(message, message.getFrom());
    Game game = sessionFactory.getGame(message.getChatId());
    if (game != null && game.isStarted()) {
      telegramClient.sendTextMessage(message.getChatId(), "Can't join during the battle!");
    } else {
      if (sessionFactory.getSession(sessionId) == null) {
        characterRepository.findByName(playerName)
            .ifPresentOrElse(
                character -> createNewSession(message, playerName, sessionId, character),
                () -> createNewSessionAndCharacter(message, playerName, sessionId));
      } else {
        game = sessionFactory.getGameOrCreate(message.getChatId());
        game.createPlayerCharacter(sessionFactory.getSession(sessionId).getSubject());
        telegramClient.sendTextMessage(message.getChatId(), playerName + ": You've joined next battle!");
      }
    }
  }

  private void createNewSession(Message message, String playerName, String sessionId, PlayerCharacter character) {
    Subject subject = subjectFactory.fromCharacter(character);
    sessionFactory.createSession(sessionId, playerName, subject);
    Game game = sessionFactory.getGameOrCreate(message.getChatId());
    game.createPlayerCharacter(subject);
    telegramClient.sendTextMessage(message.getChatId(), playerName + ": Joining with existing character.\n" + subject);
  }

  void createNewSessionAndCharacter(Message message, String playerName, String sessionId) {
    Session session = sessionFactory.createCharacterCreationCommunicates(sessionId, playerName);
    Communicate communicate = session.getNextCommunicate().get();
    telegramClient.sendReplyKeyboard(communicate, message.getChatId(), message.getMessageId());
  }
}
