package dbryla.game.yetanotherengine.cli;

import static dbryla.game.yetanotherengine.domain.subject.CharacterClass.FIGHTER;
import static dbryla.game.yetanotherengine.domain.subject.CharacterClass.WIZARD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.game.GameOptions;
import dbryla.game.yetanotherengine.domain.operations.OperationType;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.AbilityScoresSupplier;
import dbryla.game.yetanotherengine.domain.subject.CharacterClass;
import dbryla.game.yetanotherengine.domain.subject.Race;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.equipment.Armor;
import dbryla.game.yetanotherengine.domain.equipment.Weapon;
import dbryla.game.yetanotherengine.domain.subject.SubjectProperties;
import java.util.Arrays;
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
  private AbilityScoresSupplier abilityScoresSupplier;

  @Test
  void shouldReturnAvailableClasses() {
    when(gameOptions.getAvailableClasses()).thenReturn(Set.of(FIGHTER));

    List<CharacterClass> classes = consolePresenter.showAvailableClasses();

    assertThat(classes).contains(FIGHTER);
  }

  @Test
  void shouldReturnAvailableSpellsForSubject() {
    SubjectProperties subject = mock(SubjectProperties.class);
    Game game = mock(Game.class);
    when(game.getPossibleTargets(any(Subject.class), any(Spell.class))).thenReturn(List.of(""));
    when(subject.getCharacterClass()).thenReturn(WIZARD);

    List<Spell> spells = consolePresenter.showAvailableSpells(game, new Subject(subject, null));

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
    when(subject.isAbleToMove()).thenReturn(true);
    Game game = mock(Game.class);
    when(game.getAvailableWeaponsForAttack(eq(subject))).thenReturn(List.of(Weapon.SHORTSWORD));
    when(game.getAvailableSpellsForCast(eq(subject))).thenReturn(List.of(Spell.SACRED_FLAME));
    when(subject.isSpellCaster()).thenReturn(true);

    List<OperationType> operations = consolePresenter.showAvailableOperations(game, subject, false);

    assertThat(operations).contains(OperationType.ATTACK, OperationType.SPELL_CAST);
  }


  @Test
  void shouldReturnAvailableOperationsForFighter() {
    Subject subject = mock(Subject.class);
    when(subject.isSpellCaster()).thenReturn(false);
    Game game = mock(Game.class);
    when(game.getAvailableWeaponsForAttack(eq(subject))).thenReturn(List.of(Weapon.SHORTSWORD));

    List<OperationType> operations = consolePresenter.showAvailableOperations(game, subject, false);

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

  @Test
  void shouldReturnGeneratedAbilityScores() {
    List<Integer> abilityScores = List.of(1, 2, 3, 4, 5, 6);
    when(abilityScoresSupplier.get()).thenReturn(abilityScores);

    List<Integer> result = consolePresenter.showGeneratedAbilityScores();

    assertThat(result).isEqualTo(abilityScores);
  }

  @Test
  void shouldReturnAvailableRaces() {
    Set<Race> availableRaces = Set.of(Race.values());
    when(gameOptions.getAvailableRaces()).thenReturn(availableRaces);

    List<Race> result = consolePresenter.showAvailableRaces();

    assertThat(result).hasSameElementsAs(availableRaces);
  }

  @Test
  void shouldReturnAvailableWeaponsToAttackWith() {
    List<Weapon> availableWeapons = List.of(Weapon.values());
    Subject subject = mock(Subject.class);
    Game game = mock(Game.class);
    when(game.getAvailableWeaponsForAttack(eq(subject))).thenReturn(availableWeapons);

    List<Weapon> result = consolePresenter.showAvailableWeaponsToAttackWith(game, subject);

    assertThat(result).isEqualTo(availableWeapons);
  }

  @Test
  void shouldReturnAvailableWizardCantrips() {
    List<Spell> result = consolePresenter.showAvailableWizardCantrips();

    assertThat(result).contains(Spell.of(WIZARD, 0).toArray(Spell[]::new));
  }

}