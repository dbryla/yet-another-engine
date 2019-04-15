package dbryla.game.yetanotherengine.telegram;

import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class FightFactory {

  public static final String TARGET = "Choose your target";
  public static final String SPELL = "Choose your spell";

  public Optional<Communicate> targetCommunicate(Game game) {
    return targetCommunicate(game, false);
  }

  Optional<Communicate> targetCommunicate(Game game, boolean friendlyTargets) {
    return targetCommunicate(game, friendlyTargets, List.of());
  }

  Optional<Communicate> targetCommunicate(Game game, boolean friendlyTargets, List<String> ignoreTargets) {
    List<String> alive = game.getAllAliveSubjectNames(friendlyTargets);
    if (alive.size() == 1) {
      return Optional.empty();
    }
    AtomicInteger counter = new AtomicInteger();
    Collection<List<InlineKeyboardButton>> values = alive.stream()
        .filter(subject -> !ignoreTargets.contains(subject))
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
}
