package dbryla.game.yetanotherengine.cli;

import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.game.GameOptions;
import dbryla.game.yetanotherengine.domain.operations.OperationType;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.CharacterClass;
import dbryla.game.yetanotherengine.domain.subject.Race;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static dbryla.game.yetanotherengine.domain.subject.CharacterClass.FIGHTER;
import static dbryla.game.yetanotherengine.domain.subject.CharacterClass.WIZARD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsolePresenterTest {

  @InjectMocks
  private ConsolePresenter consolePresenter;

  @Mock
  private GameOptions gameOptions;

  @Test
  void shouldReturnAvailableClasses() {
    when(gameOptions.getAvailableClasses()).thenReturn(Set.of(FIGHTER));

    List<CharacterClass> classes = consolePresenter.showAvailableClasses();

    assertThat(classes).contains(FIGHTER);
  }

  @Test
  void shouldReturnAvailableSpellsForSubject() {
    Subject subject = mock(Subject.class);
    Game game = mock(Game.class);
    when(game.getPossibleTargets(any(), any(Spell.class))).thenReturn(List.of(""));
    when(subject.getCharacterClass()).thenReturn(WIZARD);

    List<Spell> spells = consolePresenter.showAvailableSpells(game, subject);

    assertThat(spells)
        .contains(Arrays.stream(Spell.values()).filter(spell -> spell.forClass(WIZARD)).toArray(Spell[]::new));
  }

  @Test
  void shouldReturnAvailableWeapons() {
    when(gameOptions.getAvailableWeapons(any(), any())).thenReturn(Set.of(Weapon.values()));

    List<Weapon> weapons = consolePresenter.showAvailableWeapons(WIZARD, Race.HUMAN);

    assertThat(weapons).contains(Weapon.values());
  }

  @Test
  void shouldReturnAvailableOperationsForSubject() {
    Subject subject = mock(Subject.class);
    Game game = mock(Game.class);
    when(game.getAvailableWeaponsForAttack(eq(subject))).thenReturn(List.of(Weapon.SHORTSWORD));
    when(game.getAvailableSpellsForCast(eq(subject))).thenReturn(List.of(Spell.SACRED_FLAME));
    when(subject.isSpellCaster()).thenReturn(true);

    List<OperationType> operations = consolePresenter.showAvailableOperations(game, subject);

    assertThat(operations).contains(OperationType.ATTACK, OperationType.SPELL_CAST);
  }


  @Test
  void shouldReturnAvailableOperationsForFighter() {
    Subject subject = mock(Subject.class);
    Game game = mock(Game.class);
    when(game.getAvailableWeaponsForAttack(eq(subject))).thenReturn(List.of(Weapon.SHORTSWORD));

    List<OperationType> operations = consolePresenter.showAvailableOperations(game, subject);

    assertThat(operations).contains(OperationType.ATTACK);
  }

  @Test
  void shouldReturnAvailableTargets() {
    Game game = mock(Game.class);
    when(game.getAllAliveEnemyNames()).thenReturn(List.of("enemy"));

    List<String> targets = consolePresenter.showAvailableEnemyTargets(game);

    assertThat(targets).contains("enemy");
  }

  @Test
  void shouldReturnAvailableShield() {
    List<Armor> shield = consolePresenter.showAvailableShield();

    assertThat(shield.size()).isEqualTo(1);
    assertThat(shield.get(0)).isEqualTo(Armor.SHIELD);
  }

  @Test
  void shouldReturnAvailableArmor() {
    when(gameOptions.getAvailableArmors(any(), any())).thenReturn(Set.of(Armor.values()));

    List<Armor> armors = consolePresenter.showAvailableArmors(FIGHTER, Race.HUMAN);

    assertThat(armors).contains(Armor.values());
  }

}