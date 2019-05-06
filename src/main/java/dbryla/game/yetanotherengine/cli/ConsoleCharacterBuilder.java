package dbryla.game.yetanotherengine.cli;

import dbryla.game.yetanotherengine.db.CharacterRepository;
import dbryla.game.yetanotherengine.db.PlayerCharacter;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.*;
import dbryla.game.yetanotherengine.domain.subject.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.*;

import static dbryla.game.yetanotherengine.domain.subject.Affiliation.PLAYERS;

@Component
@Profile("cli")
@AllArgsConstructor
class ConsoleCharacterBuilder {

  private final ConsolePresenter presenter;
  private final ConsoleInputProvider inputProvider;
  private final ConsoleAbilitiesProvider consoleAbilitiesProvider;
  private final CharacterRepository characterRepository;
  private final SubjectFactory subjectFactory;
  private final Environment environment;

  Subject createPlayer() {
    System.out.println("Would you like to create new character (0) or load existing one (1) ?");
    int playerChoice = inputProvider.cmdLineToOption();
    System.out.println("Type your character name and press enter.");
    String playerName = inputProvider.cmdLine();
    if (playerChoice == 1) {
      Optional<PlayerCharacter> character = characterRepository.findByName(playerName);
      if (character.isPresent()) {
        return subjectFactory.fromCharacter(character.get());
      }
      System.out.println("Character doesn't exist in database, falling back to character creation.");
    }
    CharacterClass characterClass = chooseClass();
    Race race = chooseRace();
    Subject subject = buildSubject(playerName, characterClass, race);
    if (Arrays.stream(environment.getActiveProfiles()).noneMatch("offline"::equals)) {
      characterRepository.findByName(playerName).ifPresent(characterRepository::delete);
      characterRepository.save(subjectFactory.toCharacter(subject));
    }
    return subject;
  }

  private CharacterClass chooseClass() {
    List<CharacterClass> availableClasses = presenter.showAvailableClasses();
    int playerChoice = inputProvider.cmdLineToOption();
    return availableClasses.get(playerChoice);
  }

  private Race chooseRace() {
    List<Race> availableRaces = presenter.showAvailableRaces();
    int playerChoice = inputProvider.cmdLineToOption();
    return availableRaces.get(playerChoice);
  }

  private Subject buildSubject(String playerName, CharacterClass characterClass, Race race) {
    System.out.println("Do you want (0) manual or (1) automatic abilities assignment?");
    int playerChoice = inputProvider.cmdLineToOption();
    Abilities abilities = getAbilities(playerChoice);
    abilities = modifyAbilitiesIfApplicable(race, abilities);
    Optional<Spell> spell = getSpell(race);
    List<Weapon> weapons = getWeapons(characterClass, race);
    Armor shield = getShield(characterClass, weapons);
    Armor armor = getArmor(characterClass, race);
    return subjectFactory.createNewSubject(playerName, race, characterClass, PLAYERS,
        abilities, weapons, armor, shield, spell.map(List::of).orElse(List.of()));
  }

  private Abilities modifyAbilitiesIfApplicable(Race race, Abilities abilities) {
    if (race.getBuildingRaceTrait() != null && BuildingRaceTrait.TWO_ADDITIONAL_ABILITY_POINTS.equals(race.getBuildingRaceTrait())) {
      presenter.showAvailableAbilitiesToImprove(-1);
      int firstChoice = inputProvider.cmdLineToOption();
      presenter.showAvailableAbilitiesToImprove(firstChoice);
      int secondChoice = inputProvider.cmdLineToOption();
      return abilities.of(firstChoice, secondChoice);
    }
    return abilities;
  }

  private Abilities getAbilities(int playerChoice) {
    if (playerChoice == 0) {
      return consoleAbilitiesProvider.getAbilities();
    } else {
      return new Abilities(12, 12, 12, 12, 12, 12);
    }
  }

  private Optional<Spell> getSpell(Race race) {
    if (race.getBuildingRaceTrait() != null && BuildingRaceTrait.WIZARD_CANTRIP.equals(race.getBuildingRaceTrait())) {
      List<Spell> spells = presenter.showAvailableWizardCantrips();
      if (!spells.isEmpty()) {
        int option = inputProvider.cmdLineToOption();
        return Optional.of(spells.get(option));
      }
    }
    return Optional.empty();
  }

  private List<Weapon> getWeapons(CharacterClass characterClass, Race race) {
    List<Weapon> availableWeapons = presenter.showAvailableWeapons(characterClass, race);
    if (availableWeapons.isEmpty()) {
      return List.of();
    }
    return List.of(availableWeapons.get(inputProvider.cmdLineToOption()), availableWeapons.get(inputProvider.cmdLineToOption()));
  }

  private Armor getShield(CharacterClass characterClass, List<Weapon> weapons) {
    if (!characterClass.getArmorProficiencies().contains(Armor.SHIELD) || weapons.stream().noneMatch(Weapon::isEligibleForShield)) {
      return null;
    }
    List<Armor> shield = presenter.showAvailableShield();
    return shield.get(0);
  }

  private Armor getArmor(CharacterClass characterClass, Race race) {
    List<Armor> availableArmors = presenter.showAvailableArmors(characterClass, race);
    if (availableArmors.isEmpty()) {
      return null;
    }
    int playerChoice = inputProvider.cmdLineToOption();
    return availableArmors.get(playerChoice);
  }

}
