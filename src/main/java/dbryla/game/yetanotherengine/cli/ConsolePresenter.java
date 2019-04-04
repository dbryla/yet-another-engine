package dbryla.game.yetanotherengine.cli;

import dbryla.game.yetanotherengine.domain.GameOptions;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.state.storage.StateStorage;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.domain.subjects.Weapon;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ConsolePresenter {

  public static final String CHOICE_FORMAT = " (%d) %s";
  private final StateStorage stateStorage;
  private final GameOptions gameOptions;

  public void showStatus() {
    StreamSupport.stream(stateStorage.findAll().spliterator(), false)
        .collect(Collectors.groupingBy(Subject::getAffiliation))
        .forEach((team, subjects) ->
            subjects.stream()
                .filter(subject -> subject.getHealthPoints() > 0)
                .map(subject -> String.format("%s(%d)", subject.getName(), subject.getHealthPoints()))
                .reduce((left, right) -> left + " " + right)
                .ifPresent(status -> System.out.print("| " + status + " |")));
    System.out.println();
  }

  public Map<Integer, Class> showAvailableClasses() {
    HashMap<Integer, Class> classesMapping = new HashMap<>();
    StringBuilder communicate = new StringBuilder("Choose your class:");
    int i = 0;
    for (Class clazz : gameOptions.getAvailableClasses()) {
      communicate.append(String.format(CHOICE_FORMAT, ++i, clazz.getSimpleName()));
      classesMapping.put(i, clazz);
    }
    System.out.println(communicate.toString());
    return classesMapping;
  }

  public Map<Integer, Weapon> showAvailableWeapons() {
    HashMap<Integer, Weapon> weaponsMapping = new HashMap<>();
    StringBuilder communicate = new StringBuilder("Choose your weapon:");
    int i = 0;
    for (Weapon weapon : Weapon.values()) {
      communicate.append(String.format(CHOICE_FORMAT, ++i, weapon.name().toLowerCase()));
      weaponsMapping.put(i, weapon);
    }
    System.out.println(communicate.toString());
    return weaponsMapping;
  }

  public Map<Integer, Spell> showAvailableSpells() {
    HashMap<Integer, Spell> spellsMapping = new HashMap<>();
    StringBuilder communicate = new StringBuilder("Choose your spell:");
    int i = 0;
    for (Spell spell : Spell.values()) {
      communicate.append(String.format(CHOICE_FORMAT, ++i, spell.name().toLowerCase().replace("_", " ")));
      spellsMapping.put(i, spell);
    }
    System.out.println(communicate.toString());
    return spellsMapping;
  }
}
