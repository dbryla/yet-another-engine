package dbryla.game.yetanotherengine.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import dbryla.game.yetanotherengine.Presenter;
import dbryla.game.yetanotherengine.domain.IncorrectStateException;
import dbryla.game.yetanotherengine.domain.subjects.classes.Subject;
import dbryla.game.yetanotherengine.domain.subjects.classes.Fighter;
import dbryla.game.yetanotherengine.domain.subjects.classes.Wizard;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConsoleCharacterBuilderTest {

  @InjectMocks
  private ConsoleCharacterBuilder consoleCharacterBuilder;

  @Mock
  private ConsoleInputProvider inputProvider;

  @Mock
  private Presenter presenter;

  @BeforeEach
  public void setUp() {
    when(presenter.showAvailableClasses()).thenReturn(List.of(Fighter.class, Wizard.class));
    when(inputProvider.cmdLine()).thenReturn("Player");
  }

  @Test
  void shouldCreateFighter() {
    when(presenter.showAvailableWeapons(any())).thenReturn(List.of());
    when(inputProvider.cmdLineToOption()).thenReturn(0);

    Subject player = consoleCharacterBuilder.createPlayer();

    assertThat(player).isInstanceOf(Fighter.class);
  }

  @Test
  void shouldCreateMage() {
    when(presenter.showAvailableWeapons(any())).thenReturn(List.of());
    when(presenter.showAvailableSpells()).thenReturn(List.of());
    when(inputProvider.cmdLineToOption()).thenReturn(1);

    Subject player = consoleCharacterBuilder.createPlayer();

    assertThat(player).isInstanceOf(Wizard.class);
  }

  @Test
  void shouldThrowExceptionWhenWrongOptionIsProvided() {
    when(presenter.showAvailableClasses()).thenReturn(List.of(Subject.class));
    when(inputProvider.cmdLineToOption()).thenReturn(0);

    assertThrows(IncorrectStateException.class, () -> consoleCharacterBuilder.createPlayer());
  }
}