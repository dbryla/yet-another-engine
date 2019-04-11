package dbryla.game.yetanotherengine.telegram;

import dbryla.game.yetanotherengine.domain.Game;
import dbryla.game.yetanotherengine.domain.GameOptions;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class FightFactory {

  public static final String TARGET = "Choose your target: ";
  public static final String SPELL = "Choose your spell";

  public Optional<Communicate> targetCommunicate(Game game) {
    List<String> allAliveEnemies = game.getAllAliveEnemies();
    if (allAliveEnemies.size() == 1) {
      return Optional.empty();
    }
    List<InlineKeyboardButton> keyboardButtons = allAliveEnemies.stream()
        .map(enemy -> new InlineKeyboardButton(enemy).setCallbackData(enemy))
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
