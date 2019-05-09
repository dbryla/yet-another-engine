package dbryla.game.yetanotherengine.telegram.callback;

import dbryla.game.yetanotherengine.db.CharacterRepository;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.SubjectFactory;
import dbryla.game.yetanotherengine.session.BuildSession;
import dbryla.game.yetanotherengine.telegram.SessionFactory;
import dbryla.game.yetanotherengine.telegram.TelegramClient;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Profile("tg")
public class CharacterCreationCallbackHandler implements CallbackHandler {

  private final TelegramClient telegramClient;
  private final SessionFactory sessionFactory;
  private final SubjectFactory subjectFactory;
  private final CharacterRepository characterRepository;

  @Override
  public void execute(Callback callback) {
    BuildSession session = sessionFactory.getBuildSession(callback.getSessionId());
    Subject subject = subjectFactory.fromSession(session);
    sessionFactory.createFightSession(callback.getSessionId(), callback.getPlayerName(), subject);
    Game game = sessionFactory.getGameOrCreate(callback.getChatId());
    game.createPlayerCharacter(subject);
    telegramClient.sendTextMessage(callback.getChatId(), session.getPlayerName() + ": Your character has been created.\n" + subject);
    characterRepository.findByName(session.getPlayerName()).ifPresent(characterRepository::delete);
    characterRepository.save(subjectFactory.toCharacter(subject));
  }
}
