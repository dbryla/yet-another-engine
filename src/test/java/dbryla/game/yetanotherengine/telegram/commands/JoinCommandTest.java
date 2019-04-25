package dbryla.game.yetanotherengine.telegram.commands;

import dbryla.game.yetanotherengine.db.CharacterRepository;
import dbryla.game.yetanotherengine.db.PlayerCharacter;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.SubjectFactory;
import dbryla.game.yetanotherengine.session.Session;
import dbryla.game.yetanotherengine.telegram.Communicate;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JoinCommandTest extends CommandTestSetup {

  @InjectMocks
  private JoinCommand joinCommand;

  @Mock
  private CharacterRepository characterRepository;

  @Mock
  private SubjectFactory subjectFactory;

  @Test
  void shouldSendMessageIfGameAlreadyStarted() {
    Game game = mock(Game.class);
    when(sessionFactory.getGame(any())).thenReturn(game);
    when(game.isStarted()).thenReturn(true);

    joinCommand.execute(update);

    verify(telegramClient).sendTextMessage(any(), any());
  }

  @Test
  void shouldJoinWithLoadedCharacterIfNoSessionExists() {
    Game game = mock(Game.class);
    when(sessionFactory.getGameOrCreate(any())).thenReturn(game);
    PlayerCharacter playerCharacter = mock(PlayerCharacter.class);
    when(characterRepository.findByName(any())).thenReturn(Optional.of(playerCharacter));
    when(subjectFactory.fromCharacter(eq(playerCharacter))).thenReturn(mock(Subject.class));

    joinCommand.execute(update);

    verify(characterRepository).findByName(any());
    verify(sessionFactory).getGameOrCreate(any());
  }

  @Test
  void shouldJoinWithExistingSession() {
    Session session = mock(Session.class);
    when(sessionFactory.getSession(any())).thenReturn(session);
    Game game = mock(Game.class);
    when(sessionFactory.getGameOrCreate(message.getChatId())).thenReturn(game);

    joinCommand.execute(update);

    verifyZeroInteractions(characterRepository);
    verify(sessionFactory).getGameOrCreate(any());
  }

  @Test
  void shouldJoinWithCreatingNewCharacterIfNoSessionAndCharacterExist() {
    Session session = mock(Session.class);
    when(sessionFactory.createCharacterCreationCommunicates(any(), any())).thenReturn(session);
    when(session.getNextBuildingCommunicate()).thenReturn(Optional.of(mock(Communicate.class)));

    joinCommand.execute(update);

    verify(characterRepository).findByName(any());
    verify(sessionFactory).createCharacterCreationCommunicates(any(), any());
  }
}