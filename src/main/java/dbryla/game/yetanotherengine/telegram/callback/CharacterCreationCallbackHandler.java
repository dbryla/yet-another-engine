package dbryla.game.yetanotherengine.telegram.callback;

import dbryla.game.yetanotherengine.db.CharacterRepository;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.SubjectFactory;
import dbryla.game.yetanotherengine.session.Session;
import dbryla.game.yetanotherengine.telegram.Commons;
import dbryla.game.yetanotherengine.telegram.SessionFactory;
import dbryla.game.yetanotherengine.telegram.TelegramClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@AllArgsConstructor
public class CharacterCreationCallbackHandler implements CallbackHandler {

  private final TelegramClient telegramClient;
  private final SessionFactory sessionFactory;
  private final SubjectFactory subjectFactory;
  private final Commons commons;
  private final CharacterRepository characterRepository;

  @Override
  public void execute(Update update) {
    Long chatId = update.getCallbackQuery().getMessage().getChatId();
    Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
    telegramClient.deleteMessage(chatId, messageId);
    String sessionId = commons.getSessionId(update.getCallbackQuery().getMessage(), update.getCallbackQuery().getFrom());
    Session session = sessionFactory.getSession(sessionId);
    // if (session.getSubject() == null) { is this needed?
    Subject subject = subjectFactory.fromSession(session);
    session.setSubject(subject);
    Game game = sessionFactory.getGameOrCreate(chatId);
    game.createPlayerCharacter(subject);
    telegramClient.sendTextMessage(chatId, session.getPlayerName() + ": Your character has been created.\n" + subject);
    characterRepository.findByName(session.getPlayerName()).ifPresent(characterRepository::delete);
    characterRepository.save(subjectFactory.toCharacter(subject));
  }

}
