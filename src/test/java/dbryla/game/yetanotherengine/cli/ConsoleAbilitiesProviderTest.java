package dbryla.game.yetanotherengine.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import dbryla.game.yetanotherengine.domain.IncorrectStateException;
import dbryla.game.yetanotherengine.domain.subject.Abilities;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConsoleAbilitiesProviderTest {

  @Mock
  private ConsolePresenter consolePresenter;

  @Mock
  private ConsoleInputProvider consoleInputProvider;

  @InjectMocks
  private ConsoleAbilitiesProvider consoleAbilitiesProvider;

  @Test
  void shouldCreateAbilitiesFromGeneratedScores() {
    when(consolePresenter.showGeneratedAbilityScores()).thenReturn(new ArrayList<>(List.of(1, 2, 3, 4, 5, 6)));
    when(consoleInputProvider.cmdLineToOption()).thenReturn(1, 2, 3, 4, 5, 6);

    Abilities abilities = consoleAbilitiesProvider.getAbilities();

    assertThat(abilities)
        .extracting("strength", "dexterity", "constitution", "intelligence", "wisdom", "charisma")
        .contains(1, 2, 3, 4, 5, 6);
  }

  @Test
  void shouldThrowExceptionWhenProvidingDifferentScoreThanProvidedFromConsolePresenter() {
    when(consolePresenter.showGeneratedAbilityScores()).thenReturn(new ArrayList<>(List.of(1, 2, 3, 4, 5, 6)));
    when(consoleInputProvider.cmdLineToOption()).thenReturn(7, 2, 3, 4, 5, 6);

    assertThrows(IncorrectStateException.class, () -> consoleAbilitiesProvider.getAbilities());
  }
}