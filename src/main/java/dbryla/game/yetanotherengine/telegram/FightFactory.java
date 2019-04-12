package dbryla.game.yetanotherengine.telegram;

import dbryla.game.yetanotherengine.domain.Game;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Component
public class FightFactory {

  public static final String TARGET = "Choose your target: ";
  public static final String SPELL = "Choose your spell";

  public Optional<Communicate> targetCommunicate(Game game) {
    return targetCommunicate(game, false);
  }

  public Optional<Communicate> targetCommunicate(Game game, boolean friendlyTargets) {
    return targetCommunicate(game, friendlyTargets, List.of());
  }

  public Optional<Communicate> targetCommunicate(Game game, boolean friendlyTargets, List<String> ignoreTargets) {
    List<String> alive = game.getAllAlive(friendlyTargets);
    if (alive.size() == 1) {
      return Optional.empty();
    }
    List<InlineKeyboardButton> keyboardButtons = alive.stream()
        .filter(subject -> !ignoreTargets.contains(subject))
        .map(subject -> new InlineKeyboardButton(subject).setCallbackData(subject))
        .collect(Collectors.toList());
    return Optional.of(new Communicate(TARGET, List.of(keyboardButtons)));
  }

  public Communicate spellCommunicate(String className) {
    AtomicInteger counter = new AtomicInteger();
    Collection<List<InlineKeyboardButton>> values = Arrays.stream(Spell.values())
        .filter(spell -> spell.forClass(className))
        .map(spell -> new InlineKeyboardButton(spell.toString()).setCallbackData(spell.name()))
        .collect(Collectors.groupingBy(b -> counter.getAndIncrement() / 3))
        .values();

    return new Communicate(SPELL, new ArrayList<>(values));
  }
}
