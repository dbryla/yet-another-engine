package dbryla.game.yetanotherengine.cli;

import static dbryla.game.yetanotherengine.domain.subject.CharacterClass.FIGHTER;
import static dbryla.game.yetanotherengine.domain.subject.Race.HALF_ELF;
import static dbryla.game.yetanotherengine.domain.subject.Race.HUMAN;
import static dbryla.game.yetanotherengine.domain.equipment.Weapon.LONGBOW;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dbryla.game.yetanotherengine.db.CharacterRepository;
import dbryla.game.yetanotherengine.db.PlayerCharacter;
import dbryla.game.yetanotherengine.domain.subject.SubjectFactory;
import dbryla.game.yetanotherengine.domain.subject.SubjectProperties;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

@ExtendWith(MockitoExtension.class)
class ConsoleCharacterBuilderTest {

  @InjectMocks
  private ConsoleCharacterBuilder consoleCharacterBuilder;

  @Mock
  private ConsoleInputProvider inputProvider;

  @Mock
  private ConsolePresenter consolePresenter;

  @Mock
  private SubjectFactory subjectFactory;

  @Mock
  private CharacterRepository characterRepository;

  @Mock
  private Environment environment;

  @Test
  void shouldDelegateCreationOfNewCharacterWithAutomaticAbilitiesToSubjectFactory() {
    when(environment.getActiveProfiles()).thenReturn(new String[0]);
    when(inputProvider.cmdLineToOption()).thenReturn(0).thenReturn(0).thenReturn(0).thenReturn(1).thenReturn(0);
    when(consolePresenter.showAvailableClasses()).thenReturn(List.of(FIGHTER));
    when(consolePresenter.showAvailableRaces()).thenReturn(List.of(HUMAN));
    when(consolePresenter.showAvailableWeapons(eq(FIGHTER), eq(HUMAN))).thenReturn(List.of(LONGBOW));
    when(characterRepository.findByName(any())).thenReturn(Optional.empty());

    consoleCharacterBuilder.createPlayer();

    verify(subjectFactory).createNewSubjectProperties(any(), eq(HUMAN), eq(FIGHTER), any(), any(), any(), any(), any(), any());
  }

  @Test
  void shouldLoadCharacterFromDatabaseIfExists() {
    when(inputProvider.cmdLineToOption()).thenReturn(1);
    when(characterRepository.findByName(any())).thenReturn(Optional.of(PlayerCharacter.builder().build()));

    consoleCharacterBuilder.createPlayer();

    verify(subjectFactory).fromCharacter(any());
  }

  @Test
  void shouldAskForAbilitiesToImproveWhileCreatingNewCharacterWithHalfElfRace() {
    when(environment.getActiveProfiles()).thenReturn(new String[0]);
    when(inputProvider.cmdLineToOption()).thenReturn(0).thenReturn(0).thenReturn(0).thenReturn(1).thenReturn(0);
    when(consolePresenter.showAvailableClasses()).thenReturn(List.of(FIGHTER));
    when(consolePresenter.showAvailableRaces()).thenReturn(List.of(HALF_ELF));
    when(consolePresenter.showAvailableWeapons(eq(FIGHTER), eq(HALF_ELF))).thenReturn(List.of(LONGBOW));
    when(characterRepository.findByName(any())).thenReturn(Optional.empty());

    consoleCharacterBuilder.createPlayer();

    verify(consolePresenter, times(2)).showAvailableAbilitiesToImprove(anyInt());
  }
}