package dbryla.game.yetanotherengine.telegram;

import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class FightFactory {

  public static final String TARGETS = "Choose your target";
  public static final String SPELL = "Choose spell to cast";
  public static final String WEAPON = "Choose weapon to attack with";
  static final String MOVE = "Where do you want to move?";

  Optional<Communicate> targetCommunicate(Game game, String playerName, Weapon weapon) {
    Subject subject = game.getSubject(playerName);
    List<String> possibleTargets = game.getPossibleTargets(subject, weapon);
    return targetCommunicate(possibleTargets, List.of());
  }

  Optional<Communicate> targetCommunicate(Game game, String playerName, Spell spell, List<String> ignore) {
    Subject subject = game.getSubject(playerName);
    List<String> possibleTargets = game.getPossibleTargets(subject, spell);
    return targetCommunicate(possibleTargets, ignore);
  }

  Optional<Communicate> targetCommunicate(List<String> possibleTargets) {
    return targetCommunicate(possibleTargets, List.of());
  }

  private Optional<Communicate> targetCommunicate(List<String> possibleTargets, List<String> ignore) {
    if (possibleTargets.size() == 1) {
      return Optional.empty();
    }
    AtomicInteger counter = new AtomicInteger();
    Collection<List<InlineKeyboardButton>> values = possibleTargets.stream()
        .filter(subject -> !ignore.contains(subject))
        .map(subject -> new InlineKeyboardButton(subject).setCallbackData(subject))
        .collect(Collectors.groupingBy(b -> counter.getAndIncrement() / 2))
        .values();
    return Optional.of(new Communicate(TARGETS, new ArrayList<>(values)));
  }

  public Communicate spellCommunicate(Game game, Subject subject) {
    AtomicInteger counter = new AtomicInteger();
    Collection<List<InlineKeyboardButton>> values = game.getAvailableSpellsForCast(subject)
        .stream()
        .map(spell -> new InlineKeyboardButton(spell.toString()).setCallbackData(spell.name()))
        .collect(Collectors.groupingBy(b -> counter.getAndIncrement() / 3))
        .values();
    return new Communicate(SPELL, new ArrayList<>(values));
  }

  public Communicate moveCommunicate(Game game, Subject subject) {
    int battlegroundLocation = subject.getPosition().getBattlegroundLocation();
    List<InlineKeyboardButton> positions = new ArrayList<>();
    int backPosition = battlegroundLocation - 1;
    if (backPosition >= 0) {
      positions.add(new InlineKeyboardButton("Back").setCallbackData(String.valueOf(backPosition)));
    }
    int frontPosition = battlegroundLocation + 1;
    if (frontPosition <= 4 && !game.areEnemiesOnCurrentPosition(subject) && canMoveSoFar(game, frontPosition)) {
      positions.add(new InlineKeyboardButton("Front").setCallbackData(String.valueOf(frontPosition)));
    }
    ArrayList<List<InlineKeyboardButton>> values = new ArrayList<>();
    values.add(positions);
    return new Communicate(MOVE, values);
  }

  public Communicate weaponCommunicate(Game game, String playerName) {
    List<InlineKeyboardButton> weapons =
        game.getAvailableWeaponsForAttack(game.getSubject(playerName))
            .stream()
            .map(weapon -> new InlineKeyboardButton(weapon.toString()).setCallbackData(weapon.name()))
            .collect(Collectors.toList());
    ArrayList<List<InlineKeyboardButton>> values = new ArrayList<>();
    values.add(weapons);
    return new Communicate(WEAPON, values);
  }

  private boolean canMoveSoFar(Game game, int frontPosition) {
    return game.isStarted() || frontPosition <= 1;
  }
}
