package dbryla.game.yetanotherengine.telegram;

import static dbryla.game.yetanotherengine.telegram.CommunicateText.MOVE;
import static dbryla.game.yetanotherengine.telegram.CommunicateText.SPELL;
import static dbryla.game.yetanotherengine.telegram.CommunicateText.TARGETS;
import static dbryla.game.yetanotherengine.telegram.CommunicateText.WEAPON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@SpringBootTest
class FightFactoryTest {

  @InjectMocks
  private FightFactory fightFactory;

  @Test
  void shouldReturnCommunicateWithTargetsForWeapon() {
    Game game = mock(Game.class);
    Weapon weapon = Weapon.SHORTSWORD;
    when(game.getPossibleTargets(any(Subject.class), eq(weapon))).thenReturn(List.of("target1", "target2"));
    Subject subject = mock(Subject.class);
    when(game.getSubject(any())).thenReturn(subject);

    Optional<Communicate> communicate = fightFactory.targetCommunicate(game, null, weapon);

    assertThat(communicate).isPresent();
    assertThat(communicate.get().getText()).isEqualTo(TARGETS);
    assertThat(communicate.get().getKeyboardButtons()).isNotEmpty();
    assertThat(communicate.get().getKeyboardButtons().get(0))
        .extracting("text", "callbackData")
        .contains(Tuple.tuple("target1", "target1"), Tuple.tuple("target2", "target2"));
  }

  @Test
  void shouldReturnCommunicateWithTargetsForSpell() {
    Game game = mock(Game.class);
    Spell spell = Spell.FIRE_BOLT;
    when(game.getSubject(any())).thenReturn(mock(Subject.class));
    when(game.getPossibleTargets(any(Subject.class), eq(spell))).thenReturn(List.of("target1", "target2"));

    Optional<Communicate> communicate = fightFactory.targetCommunicate(game, null, spell, List.of());

    assertThat(communicate).isPresent();
    assertThat(communicate.get().getText()).isEqualTo(TARGETS);
    assertThat(communicate.get().getKeyboardButtons()).isNotEmpty();
    assertThat(communicate.get().getKeyboardButtons().get(0))
        .extracting("text", "callbackData")
        .contains(Tuple.tuple("target1", "target1"), Tuple.tuple("target2", "target2"));
  }

  @Test
  void shouldReturnCommunicateWithPossibleTargets() {
    Optional<Communicate> communicate = fightFactory.targetCommunicate(List.of("target1", "target2"));

    assertThat(communicate).isPresent();
    assertThat(communicate.get().getText()).isEqualTo(TARGETS);
    assertThat(communicate.get().getKeyboardButtons()).isNotEmpty();
    assertThat(communicate.get().getKeyboardButtons().get(0))
        .extracting("text", "callbackData")
        .contains(Tuple.tuple("target1", "target1"), Tuple.tuple("target2", "target2"));
  }

  @Test
  void shouldReturnSpellCommunicate() {
    Game game = mock(Game.class);
    List<Spell> spells = List.of(Spell.SACRED_FLAME, Spell.FIRE_BOLT, Spell.HEALING_WORD, Spell.BLESS);
    when(game.getAvailableSpellsForCast(any(Subject.class))).thenReturn(spells);
    Subject subject = mock(Subject.class);

    Communicate communicate = fightFactory.spellCommunicate(game, subject);

    assertThat(communicate.getText()).isEqualTo(SPELL);
    List<List<InlineKeyboardButton>> keyboardButtons = communicate.getKeyboardButtons();
    assertThat(keyboardButtons).isNotEmpty();
    keyboardButtons.forEach(row -> assertThat(row.size()).isLessThanOrEqualTo(3));
    assertThat(
        keyboardButtons.stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toList()))
        .extracting("text", "callbackData")
        .contains(
            spells.stream()
                .map(spell -> Tuple.tuple(spell.toString(), spell.name()))
                .toArray(Tuple[]::new));
  }

  @Test
  void shouldReturnCommunicateWithMove() {
    Game game = mock(Game.class);
    Subject subject = mock(Subject.class);
    when(subject.getPosition()).thenReturn(Position.PLAYERS_FRONT);
    when(game.areEnemiesOnCurrentPosition(any())).thenReturn(false);
    when(game.canMoveToPosition(any(), anyInt())).thenReturn(true);
    when(game.isStarted()).thenReturn(true);

    Optional<Communicate> communicate = fightFactory.moveCommunicate(game, subject);

    assertThat(communicate).isPresent();
    assertThat(communicate.get().getText()).isEqualTo(MOVE);
    assertThat(communicate.get().getKeyboardButtons()).isNotEmpty();
    assertThat(communicate.get().getKeyboardButtons().get(0))
        .extracting("text", "callbackData")
        .contains(Tuple.tuple("Back", "0"), Tuple.tuple("Front", "2"));
  }

  @Test
  void shouldReturnCommunicateWithWeapons() {
    Game game = mock(Game.class);
    when(game.getAvailableWeaponsForAttack(any())).thenReturn(List.of(Weapon.BATTLEAXE, Weapon.HANDAXE));

    Communicate communicate = fightFactory.weaponCommunicate(game, null);

    assertThat(communicate.getText()).isEqualTo(WEAPON);
    assertThat(communicate.getKeyboardButtons()).isNotEmpty();
    assertThat(communicate.getKeyboardButtons().get(0))
        .extracting("text", "callbackData")
        .contains(
            Tuple.tuple(Weapon.BATTLEAXE.toString(), Weapon.BATTLEAXE.name()),
            Tuple.tuple(Weapon.HANDAXE.toString(), Weapon.HANDAXE.name()));
  }
}