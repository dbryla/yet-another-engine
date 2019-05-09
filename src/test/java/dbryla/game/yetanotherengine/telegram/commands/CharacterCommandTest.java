package dbryla.game.yetanotherengine.telegram.commands;

import dbryla.game.yetanotherengine.db.CharacterRepository;
import dbryla.game.yetanotherengine.db.PlayerCharacter;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.SubjectFactory;
import dbryla.game.yetanotherengine.session.FightSession;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

class CharacterCommandTest extends CommandTestSetup {

  @InjectMocks
  private CharacterCommand characterCommand;

  @Mock
  private CharacterRepository characterRepository;

  @Mock
  private SubjectFactory subjectFactory;

  @Test
  void shouldReturnExistingCharacterFromDatabase() {
    PlayerCharacter playerCharacter = mock(PlayerCharacter.class);
    when(characterRepository.findByName(any())).thenReturn(Optional.of(playerCharacter));
    Subject subject = mock(Subject.class);
    when(subjectFactory.fromCharacter(eq(playerCharacter))).thenReturn(subject);
    FightSession session = mock(FightSession.class);
    when(sessionFactory.createFightSession(any(), any(), eq(subject))).thenReturn(session);

    characterCommand.execute(update);

    verify(characterRepository).findByName(any());
    verify(telegramClient).sendTextMessage(anyLong(), contains("Your character."));
  }

  @Test
  void shouldReturnCommunicateAboutNoCharacter() {
    PlayerCharacter playerCharacter = mock(PlayerCharacter.class);
    when(characterRepository.findByName(any())).thenReturn(Optional.of(playerCharacter));
    Subject subject = mock(Subject.class);
    when(subjectFactory.fromCharacter(eq(playerCharacter))).thenReturn(subject);

    characterCommand.execute(update);

    verify(characterRepository).findByName(any());
    verify(telegramClient).sendTextMessage(anyLong(), contains("No existing character."));
  }

  @Test
  void shouldReturnExistingCharacterFromSession() {
    FightSession session = mock(FightSession.class);
    when(sessionFactory.getFightSession(any())).thenReturn(session);

    characterCommand.execute(update);

    verifyZeroInteractions(characterRepository);
    verify(session).getSubject();
    verify(telegramClient).sendTextMessage(anyLong(), contains("Your character."));
  }
}