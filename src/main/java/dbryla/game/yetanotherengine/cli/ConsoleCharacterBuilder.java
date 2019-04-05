package dbryla.game.yetanotherengine.cli;

import static dbryla.game.yetanotherengine.domain.GameOptions.PLAYER;

import dbryla.game.yetanotherengine.Presenter;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Armor;
import dbryla.game.yetanotherengine.domain.IncorrectStateException;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Weapon;
import dbryla.game.yetanotherengine.domain.subjects.classes.Fighter;
import dbryla.game.yetanotherengine.domain.subjects.classes.Mage;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ConsoleCharacterBuilder {

  private final Presenter presenter;
  private final ConsoleInputProvider inputProvider;

  Subject createPlayer() {
    System.out.println("Type your character name and press enter to start.");
    String playerName = inputProvider.cmdLine();
    return chooseClass(playerName);
  }

  private Subject chooseClass(String playerName) {
    List<Class> availableClasses = presenter.showAvailableClasses();
    int playerChoice = inputProvider.cmdLineToOption();
    Class clazz = availableClasses.get(playerChoice);
    if (Fighter.class.equals(clazz)) {
      return buildFighter(playerName);
    } else if (Mage.class.equals(clazz)) {
      return buildMage(playerName);
    } else {
      throw new IncorrectStateException("Wrong option");
    }
  }

  private Fighter buildFighter(String playerName) {
    Fighter.Builder builder = Fighter.builder()
        .name(playerName)
        .affiliation(PLAYER);
    getWeapon(Fighter.class).ifPresent(weapon -> {
      builder.weapon(weapon);
      getShield(weapon).ifPresent(builder::shield);
    });
    getArmor(Fighter.class).ifPresent(builder::armor);
    return builder.build();
  }

  private Optional<Weapon> getWeapon(Class clazz) {
    List<Weapon> availableWeapons = presenter.showAvailableWeapons(clazz);
    if (availableWeapons.isEmpty()) {
      return Optional.empty();
    }
    int playerChoice = inputProvider.cmdLineToOption();
    return Optional.of(availableWeapons.get(playerChoice));
  }

  private Optional<Armor> getShield(Weapon weapon) {
    if (!weapon.isEligibleForShield()) {
      return Optional.empty();
    }
    List<Armor> shield = presenter.showAvailableShield();
    return Optional.of(shield.get(0));
  }

  private Optional<Armor> getArmor(Class clazz) {
    List<Armor> availableArmors = presenter.showAvailableArmors(clazz);
    if (availableArmors.isEmpty()) {
      return Optional.empty();
    }
    int playerChoice = inputProvider.cmdLineToOption();
    return Optional.of(availableArmors.get(playerChoice));
  }

  private Mage buildMage(String playerName) {
    Mage.Builder builder = Mage.builder()
        .name(playerName)
        .affiliation(PLAYER);
    getWeapon(Mage.class).ifPresent(builder::weapon);
    getSpell().ifPresent(builder::spell);
    return builder.build();
  }

  private Optional<Spell> getSpell() {
    List<Spell> availableSpells = presenter.showAvailableSpells();
    if (availableSpells.isEmpty()) {
      return Optional.empty();
    }
    int playerChoice = inputProvider.cmdLineToOption();
    return Optional.of(availableSpells.get(playerChoice));
  }

}
