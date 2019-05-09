package dbryla.game.yetanotherengine.telegram.commands;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import dbryla.game.yetanotherengine.db.CharacterRepository;
import dbryla.game.yetanotherengine.db.PlayerCharacter;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.SubjectFactory;
import dbryla.game.yetanotherengine.session.BuildSession;
import dbryla.game.yetanotherengine.session.FightSession;
import dbryla.game.yetanotherengine.telegram.Communicate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

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
    FightSession session = mock(FightSession.class);
    when(sessionFactory.getFightSession(any())).thenReturn(session);
    Game game = mock(Game.class);
    when(sessionFactory.getGameOrCreate(message.getChatId())).thenReturn(game);

    joinCommand.execute(update);

    verifyZeroInteractions(characterRepository);
    verify(sessionFactory).getGameOrCreate(any());
  }

  @Test
  void shouldJoinWithCreatingNewCharacterIfNoSessionAndCharacterExist() {
    BuildSession session = mock(BuildSession.class);
    when(sessionFactory.createBuildSession(any(), any())).thenReturn(session);
    when(session.getNextCommunicate()).thenReturn(mock(Communicate.class));

    joinCommand.execute(update);

    verify(characterRepository).findByName(any());
    verify(sessionFactory).createBuildSession(any(), any());
  }
}