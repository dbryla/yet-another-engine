package dbryla.game.yetanotherengine.cli;

import static dbryla.game.yetanotherengine.domain.GameOptions.ALLIES;

import dbryla.game.yetanotherengine.domain.Abilities;
import dbryla.game.yetanotherengine.domain.subjects.classes.Cleric;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Armor;
import dbryla.game.yetanotherengine.domain.IncorrectStateException;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Weapon;
import dbryla.game.yetanotherengine.domain.subjects.classes.Fighter;
import dbryla.game.yetanotherengine.domain.subjects.classes.Wizard;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("cli")
@AllArgsConstructor
public class ConsoleCharacterBuilder {

  private final ConsolePresenter presenter;
  private final ConsoleInputProvider inputProvider;
  private final ConsoleAbilitiesProvider consoleAbilitiesProvider;

  Subject createPlayer() {
    System.out.println("Type your character name and press enter to start.");
    String playerName = inputProvider.cmdLine();
    return chooseClass(playerName);
  }

  private Subject chooseClass(String playerName) {
    List<Class> availableClasses = presenter.showAvailableClasses();
    int playerChoice = inputProvider.cmdLineToOption();
    Class clazz = availableClasses.get(playerChoice);
    System.out.println("Do you want (0) manual or (1) automatic abilities assignment?");
    playerChoice = inputProvider.cmdLineToOption();
    Abilities abilities = getAbilities(playerChoice);
    if (Fighter.class.equals(clazz)) {
      return buildFighter(playerName, abilities);
    } else if (Wizard.class.equals(clazz)) {
      return buildWizard(playerName, abilities);
    } else if (Cleric.class.equals(clazz)) {
      return buildCleric(playerName, abilities);
    } else {
      throw new IncorrectStateException("Unsupported class: " + clazz);
    }
  }

  private Abilities getAbilities(int playerChoice) {
    if (playerChoice == 0) {
      return consoleAbilitiesProvider.getAbilities();
    } else {
      return new Abilities(12, 12, 12, 12, 12, 12);
    }
  }

  private Fighter buildFighter(String playerName, Abilities abilities) {
    Fighter.Builder builder = Fighter.builder()
        .name(playerName)
        .affiliation(ALLIES)
        .abilities(abilities);
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

  private Wizard buildWizard(String playerName, Abilities abilities) {
    Wizard.Builder builder = Wizard.builder()
        .name(playerName)
        .affiliation(ALLIES)
        .abilities(abilities);
    getWeapon(Wizard.class).ifPresent(builder::weapon);
    return builder.build();
  }

  private Subject buildCleric(String playerName, Abilities abilities) {
    Cleric.Builder builder = Cleric.builder()
        .name(playerName)
        .affiliation(ALLIES)
        .abilities(abilities);
    getWeapon(Cleric.class).ifPresent(weapon -> {
      builder.weapon(weapon);
      getShield(weapon).ifPresent(builder::shield);
    });
    getArmor(Cleric.class).ifPresent(builder::armor);
    return builder.build();
  }

}
