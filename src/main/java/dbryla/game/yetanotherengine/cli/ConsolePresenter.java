package dbryla.game.yetanotherengine.cli;

import dbryla.game.yetanotherengine.Presenter;
import dbryla.game.yetanotherengine.domain.DiceRoll;
import dbryla.game.yetanotherengine.domain.Game;
import dbryla.game.yetanotherengine.domain.GameOptions;
import dbryla.game.yetanotherengine.domain.operations.AttackOperation;
import dbryla.game.yetanotherengine.domain.operations.Operation;
import dbryla.game.yetanotherengine.domain.operations.SpellCastOperation;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.state.storage.StateStorage;
import dbryla.game.yetanotherengine.domain.subjects.classes.Subject;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Weapon;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
  public List<Weapon> showAvailableWeapons(Class clazz) {
    List<Weapon> weapons = new LinkedList<>();
    StringBuilder communicate = new StringBuilder("Choose your weapon:");
    int i = 0;
    for (Weapon weapon : gameOptions.getAvailableWeapons(clazz)) {
      communicate.append(String.format(CHOICE_FORMAT, i++, weapon.name().toLowerCase()));
      weapons.add(weapon);
    }
    if (!weapons.isEmpty()) {
      System.out.println(communicate.toString());
    }
    return weapons;
  }

  @Override
  public List<Spell> showAvailableSpells(Class clazz) {
    List<Spell> spells = new LinkedList<>();
    StringBuilder communicate = new StringBuilder("Choose your spell:");
    Set<Spell> spellsForClass = Arrays.stream(Spell.values())
        .filter(spell -> spell.forClass(clazz))
        .collect(Collectors.toSet());
    int i = 0;
    for (Spell spell : spellsForClass) {
      communicate.append(String.format(CHOICE_FORMAT, i++, toHumanReadableName(spell.name())));
      spells.add(spell);
    }
    if (!spells.isEmpty()) {
      System.out.println(communicate.toString());
    }
    return spells;
  }

  private String toHumanReadableName(String name) {
    return name.toLowerCase().replace("_", " ");
  }

  @Override
  public List<Operation> showAvailableOperations(Class clazz) {
    List<Operation> operations = new LinkedList<>();
    StringBuilder communicate = new StringBuilder("Which action you pick:");
    communicate.append(String.format(CHOICE_FORMAT, 0, "attack"));
    operations.add(attackOperation);
    if (gameOptions.isSpellCaster(clazz)) {
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
    for (String enemy : game.getAllAliveEnemies()) {
      communicate.append(String.format(CHOICE_FORMAT, i++, enemy));
      enemies.add(enemy);
    }
    System.out.println(communicate.toString());
    return enemies;
  }

  @Override
  public List<Armor> showAvailableShield() {
    System.out.println("Added shield to your equipment.");
    return List.of(Armor.SHIELD);
  }

  @Override
  public List<Armor> showAvailableArmors(Class clazz) {
    List<Armor> armors = new LinkedList<>();
    StringBuilder communicate = new StringBuilder("Choose your armor:");
    int i = 0;
    for (Armor armor : gameOptions.getAvailableArmors(clazz)) {
      communicate.append(String.format(CHOICE_FORMAT, i++, toHumanReadableName(armor.name())));
      armors.add(armor);
    }
    if (!armors.isEmpty()) {
      System.out.println(communicate.toString());
    }
    return armors;
  }

  @Override
  public List<Integer> showGeneratedAbilityScores() {
    List<Integer> abilityScores = new LinkedList<>();
    for (int i = 0; i < 6; i++) {
      List<Integer> rolls = new LinkedList<>();
      for (int j = 0; j < 4; j++) {
        rolls.add(DiceRoll.k6());
      }
      rolls.stream().sorted().skip(1).reduce(Integer::sum).ifPresent(abilityScores::add);
    }
    System.out.println("Your ability scores: ");
    abilityScores.forEach(abilityScore -> System.out.print(abilityScore + " "));
    System.out.println();
    System.out.println("Please assign each of them respectively to abilities: Str, Dex, Con, Int, Wis, Cha");
    return abilityScores;
  }

}
