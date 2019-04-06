package dbryla.game.yetanotherengine.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dbryla.game.yetanotherengine.domain.Game;
import dbryla.game.yetanotherengine.domain.GameOptions;
import dbryla.game.yetanotherengine.domain.operations.AttackOperation;
import dbryla.game.yetanotherengine.domain.operations.Operation;
import dbryla.game.yetanotherengine.domain.operations.SpellCastOperation;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subjects.classes.Subject;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Weapon;
import dbryla.game.yetanotherengine.domain.subjects.classes.Fighter;
import dbryla.game.yetanotherengine.domain.subjects.classes.Wizard;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConsolePresenterTest {

  @InjectMocks
  private ConsolePresenter consolePresenter;

  @Mock
  private GameOptions gameOptions;

  @Mock
  private AttackOperation attackOperation;

  @Mock
  private SpellCastOperation spellCastOperation;

  @Test
  void shouldReturnAvailableClasses() {
    when(gameOptions.getAvailableClasses()).thenReturn(Set.of(Fighter.class));

    List<Class> classes = consolePresenter.showAvailableClasses();

    assertThat(classes).contains(Fighter.class);
  }

  @Test
  void shouldReturnAvailableSpells() {
    List<Spell> spells = consolePresenter.showAvailableSpells();

    assertThat(spells).contains(Spell.values());
  }

  @Test
  void shouldReturnAvailableWeapons() {
    when(gameOptions.getAvailableWeapons(any())).thenReturn(Set.of(Weapon.values()));

    List<Weapon> weapons = consolePresenter.showAvailableWeapons(Fighter.class);

    assertThat(weapons).contains(Weapon.values());
  }

  @Test
  void shouldReturnAvailableOperationsForMage() {
    Subject mage = mock(Wizard.class);

    List<Operation> operations = consolePresenter.showAvailableOperations(mage);

    assertThat(operations).contains(attackOperation, spellCastOperation);
  }


  @Test
  void shouldReturnAvailableOperationsForFighter() {
    Subject fighter = mock(Fighter.class);

    List<Operation> operations = consolePresenter.showAvailableOperations(fighter);

    assertThat(operations).contains(attackOperation);
  }

  @Test
  void shouldReturnAvailableTargets() {
    Game game = mock(Game.class);
    when(game.getAllAliveEnemies()).thenReturn(List.of("enemy"));

    List<String> targets = consolePresenter.showAvailableTargets(game);

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
    when(gameOptions.getAvailableArmors(any())).thenReturn(Set.of(Armor.values()));

    List<Armor> armors = consolePresenter.showAvailableArmors(Fighter.class);

    assertThat(armors).contains(Armor.values());
  }

}