package dbryla.game.yetanotherengine.cli;

import dbryla.game.yetanotherengine.Presenter;
import dbryla.game.yetanotherengine.domain.Game;
import dbryla.game.yetanotherengine.domain.GameOptions;
import dbryla.game.yetanotherengine.domain.operations.AttackOperation;
import dbryla.game.yetanotherengine.domain.operations.Operation;
import dbryla.game.yetanotherengine.domain.operations.SpellCastOperation;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.state.storage.StateStorage;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.domain.subjects.Weapon;
import dbryla.game.yetanotherengine.domain.subjects.classes.Mage;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ConsolePresenter implements Presenter {

  private static final String CHOICE_FORMAT = " (%d) %s";
  private final StateStorage stateStorage;
  private final GameOptions gameOptions;
  private final AttackOperation attackOperation;
  private final SpellCastOperation spellCastOperation;

  @Override
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

  @Override
  public List<Class> showAvailableClasses() {
    List<Class> classes = new LinkedList<>();
    StringBuilder communicate = new StringBuilder("Choose your class:");
    int i = 0;
    for (Class clazz : gameOptions.getAvailableClasses()) {
      communicate.append(String.format(CHOICE_FORMAT, i++, clazz.getSimpleName()));
      classes.add(clazz);
    }
    System.out.println(communicate.toString());
    return classes;
  }

  @Override
  public List<Weapon> showAvailableWeapons() {
    List<Weapon> weapons = new LinkedList<>();
    StringBuilder communicate = new StringBuilder("Choose your weapon:");
    int i = 0;
    for (Weapon weapon : Weapon.values()) {
      communicate.append(String.format(CHOICE_FORMAT, i++, weapon.name().toLowerCase()));
      weapons.add(weapon);
    }
    System.out.println(communicate.toString());
    return weapons;
  }

  @Override
  public List<Spell> showAvailableSpells() {
    List<Spell> spells = new LinkedList<>();
    StringBuilder communicate = new StringBuilder("Choose your spell:");
    int i = 0;
    for (Spell spell : Spell.values()) {
      communicate.append(String.format(CHOICE_FORMAT, i++, spell.name().toLowerCase().replace("_", " ")));
      spells.add(spell);
    }
    System.out.println(communicate.toString());
    return spells;
  }

  @Override
  public List<Operation> showAvailableOperations(Subject subject) {
    List<Operation> operations = new LinkedList<>();
    StringBuilder communicate = new StringBuilder("Which action you pick:");
    communicate.append(String.format(CHOICE_FORMAT, 0, "attack"));
    operations.add(attackOperation);
    if (subject instanceof Mage) {
      communicate.append(String.format(CHOICE_FORMAT, 1, "spell"));
      operations.add(spellCastOperation);
    }
    System.out.println(communicate.toString());
    return operations;
  }

  @Override
  public List<String> showAvailableTargets(Game game) {
    List<String> enemies = new LinkedList<>();
    StringBuilder communicate = new StringBuilder("Choose your target:");
    int i = 0;
    for (String enemy : game.getAllEnemies()) {
      communicate.append(String.format(CHOICE_FORMAT, i++, enemy));
      enemies.add(enemy);
    }
    System.out.println(communicate.toString());
    return enemies;
  }

}
