package dbryla.game.yetanotherengine.telegram;

import dbryla.game.yetanotherengine.domain.game.GameOptions;
import dbryla.game.yetanotherengine.domain.subject.CharacterClass;
import dbryla.game.yetanotherengine.domain.subject.Race;
import dbryla.game.yetanotherengine.domain.subject.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import dbryla.game.yetanotherengine.session.Session;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
@Profile("tg")
public class BuildingFactory {

  public static final String CLASS = "Choose a class:";
  public static final String RACE = "Choose a race:";
  public static final String ABILITIES = "Assign scores to your abilities: Str, Dex, Con, Int, Wis, Cha";
  public static final String WEAPONS = "Choose your weapon:";
  public static final String ARMOR = "Choose your armor:";

  private final GameOptions gameOptions;

  Communicate chooseClassCommunicate() {
    ArrayList<CharacterClass> classes = new ArrayList<>(gameOptions.getAvailableClasses());
    List<InlineKeyboardButton> keyboardButtons = new LinkedList<>();
    classes.forEach(characterClass -> keyboardButtons.add(
        new InlineKeyboardButton(characterClass.toString()).setCallbackData(characterClass.name())));
    return new Communicate(CLASS, List.of(keyboardButtons));
  }

  Communicate chooseRaceGroupCommunicate() {
    ArrayList<String> races = new ArrayList<>(gameOptions.getAvailableRaceGroups());
    AtomicInteger counter = new AtomicInteger();
    Collection<List<InlineKeyboardButton>> values = races.stream()
        .map(race -> new InlineKeyboardButton(race).setCallbackData(race.toUpperCase().replace("-", "_")))
        .collect(Collectors.groupingBy(b -> counter.getAndIncrement() / 3))
        .values();
    return new Communicate(RACE, new ArrayList<>(values));
  }

  Communicate chooseRaceCommunicate(String raceGroup) {
    List<InlineKeyboardButton> values = gameOptions.getAvailableRaces()
        .stream()
        .filter(race -> raceGroup.equalsIgnoreCase(race.getDisplayName()))
        .map(race -> new InlineKeyboardButton(race.toString()).setCallbackData(race.name()))
        .collect(Collectors.toList());
    return new Communicate(RACE, List.of(values));
  }

  Communicate assignAbilitiesCommunicate(List<Integer> scores) {
    List<List<InlineKeyboardButton>> keyboardButtons = createKeyboardButtons(scores);
    return new Communicate(ABILITIES, keyboardButtons);
  }

  private List<List<InlineKeyboardButton>> createKeyboardButtons(List<Integer> scores) {
    List<InlineKeyboardButton> keyboardButtons = new LinkedList<>();
    for (Integer integer : scores) {
      String score = String.valueOf(integer);
      keyboardButtons.add(new InlineKeyboardButton().setText(score).setCallbackData(score));
    }
    return List.of(keyboardButtons);
  }

  Optional<Communicate> nextAbilityAssignment(Session session, String lastScore) {
    session.getAbilityScores().remove(Integer.valueOf(lastScore));
    if (session.getAbilityScores().isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(new Communicate(ABILITIES, createKeyboardButtons(session.getAbilityScores())));
  }

  Communicate chooseWeaponCommunicate(CharacterClass characterClass, Race race) {
    Set<Weapon> availableWeapons = gameOptions.getAvailableWeapons(characterClass, race);
    AtomicInteger counter = new AtomicInteger();
    Collection<List<InlineKeyboardButton>> values = availableWeapons.stream()
        .map(weapon -> new InlineKeyboardButton(weapon.toString()).setCallbackData(weapon.name()))
        .collect(Collectors.groupingBy(b -> counter.getAndIncrement() / 4))
        .values();
    return new Communicate(WEAPONS, new ArrayList<>(values));
  }

  Optional<Communicate> chooseArmorCommunicate(CharacterClass characterClass, Race race) {
    Set<Armor> availableArmors = gameOptions.getAvailableArmors(characterClass, race);
    if (availableArmors.isEmpty()) {
      return Optional.empty();
    }
    List<InlineKeyboardButton> keyboardButtons = availableArmors.stream()
        .map(armor -> new InlineKeyboardButton(armor.toString()).setCallbackData(armor.name()))
        .collect(Collectors.toList());
    return Optional.of(new Communicate(ARMOR, List.of(keyboardButtons)));
  }

}
