package dbryla.game.yetanotherengine.cli;

import dbryla.game.yetanotherengine.db.CharacterRepository;
import dbryla.game.yetanotherengine.domain.subject.SubjectFactory;
import dbryla.game.yetanotherengine.domain.subject.equipment.Armor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Optional;

import static dbryla.game.yetanotherengine.domain.subject.CharacterClass.FIGHTER;
import static dbryla.game.yetanotherengine.domain.subject.Race.HUMAN;
import static dbryla.game.yetanotherengine.domain.subject.equipment.Weapon.LONGBOW;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    when(consolePresenter.showAvailableShield()).thenReturn(List.of(Armor.SHIELD));
    when(characterRepository.findByName(any())).thenReturn(Optional.empty());

    consoleCharacterBuilder.createPlayer();

    verify(subjectFactory).createNewSubject(any(), eq(HUMAN), eq(FIGHTER), any(), any(), any(), any(), any());
  }
}