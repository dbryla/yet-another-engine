package dbryla.game.yetanotherengine.cli;

import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.game.GameOptions;
import dbryla.game.yetanotherengine.domain.game.state.storage.StateStorage;
import dbryla.game.yetanotherengine.domain.operations.OperationType;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.AbilityScoresSupplier;
import dbryla.game.yetanotherengine.domain.subject.CharacterClass;
import dbryla.game.yetanotherengine.domain.subject.Race;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
@Profile("cli")
public class ConsolePresenter {

  private static final String CHOICE_FORMAT = " (%d) %s";
  private final StateStorage stateStorage;
  private final GameOptions gameOptions;
  private final AbilityScoresSupplier abilityScoresSupplier;

  void showStatus(Long gameId) {
    stateStorage.findAll(gameId).stream()
        .collect(Collectors.groupingBy(Subject::getAffiliation))
        .forEach((team, subjects) ->
            subjects.stream()
                .filter(subject -> subject.getCurrentHealthPoints() > 0)
                .map(subject -> String.format("%s(%d)", subject.getName(), subject.getCurrentHealthPoints()))
                .reduce((left, right) -> left + " " + right)
                .ifPresent(status -> System.out.print("| " + status + " |")));
    System.out.println();
  }

  List<CharacterClass> showAvailableClasses() {
    List<CharacterClass> classes = new LinkedList<>();
    StringBuilder communicate = new StringBuilder("Choose your class:");
    int i = 0;
    for (CharacterClass characterClass : gameOptions.getAvailableClasses()) {
      communicate.append(String.format(CHOICE_FORMAT, i++, characterClass));
      classes.add(characterClass);
    }
    System.out.println(communicate.toString());
    return classes;
  }

  List<Weapon> showAvailableWeapons(CharacterClass characterClass, Race race) {
    List<Weapon> weapons = new LinkedList<>();
    StringBuilder communicate = new StringBuilder("Choose your two weapons:");
    int i = 0;
    for (Weapon weapon : gameOptions.getAvailableWeapons(characterClass, race)) {
      communicate.append(String.format(CHOICE_FORMAT, i++, weapon.toString()));
      weapons.add(weapon);
    }
    if (!weapons.isEmpty()) {
      System.out.println(communicate.toString());
    }
    return weapons;
  }

  List<Spell> showAvailableSpells(Game game, Subject subject) {
    List<Spell> spells = new LinkedList<>();
    StringBuilder communicate = new StringBuilder("Choose your spell:");
    Set<Spell> spellsSet = Arrays.stream(Spell.values())
        .filter(spell -> spell.forClass(subject.getCharacterClass()) && !game.getPossibleTargets(subject, spell).isEmpty())
        .collect(Collectors.toSet());
    spellsSet.addAll(subject.getSpells());
    spellsSet = spellsSet
        .stream()
        .filter(spell -> !game.getPossibleTargets(subject, spell).isEmpty())
        .collect(Collectors.toSet());
    int i = 0;
    for (Spell spell : spellsSet) {
      communicate.append(String.format(CHOICE_FORMAT, i++, spell.toString()));
      spells.add(spell);
    }
    if (!spells.isEmpty()) {
      System.out.println(communicate.toString());
    }
    return spells;
  }

  List<OperationType> showAvailableOperations(Game game, Subject subject) {
    List<OperationType> operations = new LinkedList<>();
    StringBuilder communicate = new StringBuilder("Which action you pick:");
    communicate.append(String.format(CHOICE_FORMAT, 0, "move"));
    operations.add(OperationType.MOVE);
    communicate.append(String.format(CHOICE_FORMAT, 1, "pass"));
    operations.add(OperationType.PASS);
    List<Weapon> weapons = game.getAvailableWeaponsForAttack(subject);
    if (!weapons.isEmpty()) {
      communicate.append(String.format(CHOICE_FORMAT, 2, "attack"));
      operations.add(OperationType.ATTACK);
    }
    List<Spell> spells = game.getAvailableSpellsForCast(subject);
    if (subject.isSpellCaster() && !spells.isEmpty()) {
      communicate.append(String.format(CHOICE_FORMAT, 3, "spell"));
      operations.add(OperationType.SPELL_CAST);
    }
    System.out.println(communicate.toString());
    return operations;
  }

  List<String> showAvailableEnemyTargets(Game game) {
    return showAvailableTargets(game.getAllAliveEnemyNames());
  }

  List<String> showAvailableTargets(List<String> allAliveAllies) {
    List<String> targets = new LinkedList<>();
    StringBuilder communicate = new StringBuilder("Choose your target:");
    int i = 0;
    for (String target : allAliveAllies) {
      communicate.append(String.format(CHOICE_FORMAT, i++, target));
      targets.add(target);
    }
    System.out.println(communicate.toString());
    return targets;
  }

  List<Armor> showAvailableShield() {
    System.out.println("Added shield to your equipment.");
    return List.of(Armor.SHIELD);
  }

  List<Armor> showAvailableArmors(CharacterClass characterClass, Race race) {
    List<Armor> armors = new LinkedList<>();
    StringBuilder communicate = new StringBuilder("Choose your armor:");
    int i = 0;
    for (Armor armor : gameOptions.getAvailableArmors(characterClass, race)) {
      communicate.append(String.format(CHOICE_FORMAT, i++, armor.toString()));
      armors.add(armor);
    }
    if (!armors.isEmpty()) {
      System.out.println(communicate.toString());
    }
    return armors;
  }

  List<Integer> showGeneratedAbilityScores() {
    List<Integer> abilityScores = abilityScoresSupplier.get();
    System.out.println("Your ability scores: ");
    abilityScores.forEach(abilityScore -> System.out.print(abilityScore + " "));
    System.out.println();
    System.out.println("Please assign each of them respectively to abilities: Str, Dex, Con, Int, Wis, Cha");
    return abilityScores;
  }

  List<Race> showAvailableRaces() {
    List<Race> races = new LinkedList<>();
    StringBuilder communicate = new StringBuilder("Choose your race:");
    int i = 0;
    for (Race race : gameOptions.getAvailableRaces()) {
      communicate.append(String.format(CHOICE_FORMAT, i++, race));
      races.add(race);
    }
    System.out.println(communicate.toString());
    return races;
  }

  void showAvailablePositions(Game game, Subject subject) {
    int battlegroundLocation = subject.getPosition().getBattlegroundLocation();
    StringBuilder communicate = new StringBuilder("Choose your position:");
    int backPosition = battlegroundLocation - 1;
    if (backPosition >= 0) {
      communicate.append(String.format(CHOICE_FORMAT, backPosition, "Back"));
    }
    int frontPosition = battlegroundLocation + 1;
    if (frontPosition <= 4 && !game.areEnemiesOnCurrentPosition(subject)) {
      communicate.append(String.format(CHOICE_FORMAT, frontPosition, "Front"));
    }
    System.out.println(communicate.toString());
  }

  List<Weapon> showAvailableWeaponsToAttackWith(Game game, Subject subject) {
    List<Weapon> weapons = new LinkedList<>();
    StringBuilder communicate = new StringBuilder("Choose weapon:");
    int i = 0;
    for (Weapon weapon : game.getAvailableWeaponsForAttack(subject)) {
      communicate.append(String.format(CHOICE_FORMAT, i++, weapon.toString()));
      weapons.add(weapon);
    }
    if (!weapons.isEmpty()) {
      System.out.println(communicate.toString());
    }
    return weapons;
  }

}
