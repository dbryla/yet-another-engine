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

import static dbryla.game.yetanotherengine.domain.game.GameOptions.ENEMIES;
import static dbryla.game.yetanotherengine.domain.game.GameOptions.PLAYERS;

@Component
public class FightFactory {

  public static final String TARGET = "Choose your target";
  public static final String SPELL = "Choose your spell";
  static final String MOVE = "Where do you want to move?";

  public Optional<Communicate> targetCommunicate(Game game, String playerName, Weapon weapon) {
    List<String> possibleTargets = game.getPossibleTargets(playerName, weapon);
    return targetCommunicate(possibleTargets, List.of());
  }

  Optional<Communicate> targetCommunicate(Game game, String playerName, Spell spell) {
    return targetCommunicate(game, playerName, spell, List.of());
  }

  Optional<Communicate> targetCommunicate(Game game, String playerName, Spell spell, List<String> ignore) {
    List<String> possibleTargets = game.getPossibleTargets(playerName, spell);
    return targetCommunicate(possibleTargets, ignore);
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
    return Optional.of(new Communicate(TARGET, new ArrayList<>(values)));
  }

  public Communicate spellCommunicate(Subject subject) {
    AtomicInteger counter = new AtomicInteger();
    List<Spell> spells = Arrays.stream(Spell.values())
        .filter(spell -> spell.forClass(subject.getCharacterClass())).collect(Collectors.toList());
    spells.addAll(subject.getSpells());
    Collection<List<InlineKeyboardButton>> values = spells.stream()
        .map(spell -> new InlineKeyboardButton(spell.toString()).setCallbackData(spell.name()))
        .collect(Collectors.groupingBy(b -> counter.getAndIncrement() / 3))
        .values();
    return new Communicate(SPELL, new ArrayList<>(values));
  }

  public Communicate moveCommunicate(Subject subject, Game game) {
    int battlegroundLocation = subject.getPosition().getBattlegroundLocation();
    List<InlineKeyboardButton> positions = new ArrayList<>();
    int backPosition = battlegroundLocation - 1;
    if (backPosition >= 0) {
      positions.add(new InlineKeyboardButton("Back").setCallbackData(String.valueOf(backPosition)));
    }
    int frontPosition = battlegroundLocation + 1;
    if (frontPosition <= 4) {
      if (thereIsNoEnemiesOnCurrentPosition(subject, game)) {
        positions.add(new InlineKeyboardButton("Front").setCallbackData(String.valueOf(frontPosition)));
      }
    }
    ArrayList<List<InlineKeyboardButton>> values = new ArrayList<>();
    values.add(positions);
    return new Communicate(MOVE, values);
  }

  private boolean thereIsNoEnemiesOnCurrentPosition(Subject subject, Game game) {
    return game.getSubjectsPositionsMap()
        .get(subject.getPosition())
        .stream()
        .noneMatch(anySubject -> anySubject.getAffiliation().equals(getEnemyAffiliation(subject.getAffiliation())));
  }

  private String getEnemyAffiliation(String affiliation) {
    return PLAYERS.equals(affiliation) ? ENEMIES : PLAYERS;
  }
}
