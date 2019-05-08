package dbryla.game.yetanotherengine.telegram;

import static dbryla.game.yetanotherengine.telegram.CommunicateText.ABILITIES;
import static dbryla.game.yetanotherengine.telegram.CommunicateText.ARMOR;
import static dbryla.game.yetanotherengine.telegram.CommunicateText.CLASS;
import static dbryla.game.yetanotherengine.telegram.CommunicateText.EXTRA_ABILITIES;
import static dbryla.game.yetanotherengine.telegram.CommunicateText.RACE;
import static dbryla.game.yetanotherengine.telegram.CommunicateText.SPELLS;
import static dbryla.game.yetanotherengine.telegram.CommunicateText.WEAPONS;

import dbryla.game.yetanotherengine.domain.game.GameOptions;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.BuildingRaceTrait;
import dbryla.game.yetanotherengine.domain.subject.CharacterClass;
import dbryla.game.yetanotherengine.domain.subject.Race;
import dbryla.game.yetanotherengine.domain.subject.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import dbryla.game.yetanotherengine.session.Session;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@AllArgsConstructor
@Component
@Profile("tg")
public class BuildingFactory {

  private final GameOptions gameOptions;

  public Communicate chooseClassCommunicate() {
    ArrayList<CharacterClass> classes = new ArrayList<>(gameOptions.getAvailableClasses());
    List<InlineKeyboardButton> keyboardButtons = new LinkedList<>();
    classes.forEach(characterClass -> keyboardButtons.add(
        new InlineKeyboardButton(characterClass.toString()).setCallbackData(characterClass.name())));
    return new Communicate(CLASS, List.of(keyboardButtons));
  }

  public Communicate chooseRaceGroupCommunicate() {
    ArrayList<String> races = new ArrayList<>(gameOptions.getAvailableRaceGroups());
    AtomicInteger counter = new AtomicInteger();
    Collection<List<InlineKeyboardButton>> values = races.stream()
        .map(race -> new InlineKeyboardButton(race).setCallbackData(race.toUpperCase().replace("-", "_")))
        .collect(Collectors.groupingBy(b -> counter.getAndIncrement() / 3))
        .values();
    return new Communicate(RACE, new ArrayList<>(values));
  }

  public Communicate chooseRaceCommunicate(String raceGroup) {
    List<InlineKeyboardButton> values = gameOptions.getAvailableRaces()
        .stream()
        .filter(race -> raceGroup.equalsIgnoreCase(race.getDisplayName()))
        .map(race -> new InlineKeyboardButton(race.toString()).setCallbackData(race.name()))
        .collect(Collectors.toList());
    return new Communicate(RACE, List.of(values));
  }

  public Communicate assignAbilitiesCommunicate(List<Integer> scores) {
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

  public Optional<Communicate> nextAbilityAssignment(Session session, String lastScore) {
    session.getAbilityScores().remove(Integer.valueOf(lastScore));
    if (session.getAbilityScores().isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(new Communicate(ABILITIES, createKeyboardButtons(session.getAbilityScores())));
  }

  public Communicate chooseWeaponCommunicate(CharacterClass characterClass, Race race) {
    Set<Weapon> availableWeapons = gameOptions.getAvailableWeapons(characterClass, race);
    AtomicInteger counter = new AtomicInteger();
    Collection<List<InlineKeyboardButton>> values = availableWeapons.stream()
        .map(weapon -> new InlineKeyboardButton(weapon.toString()).setCallbackData(weapon.name()))
        .collect(Collectors.groupingBy(b -> counter.getAndIncrement() / 4))
        .values();
    return new Communicate(WEAPONS, new ArrayList<>(values));
  }

  public Optional<Communicate> chooseArmorCommunicate(CharacterClass characterClass, Race race) {
    Set<Armor> availableArmors = gameOptions.getAvailableArmors(characterClass, race);
    if (availableArmors.isEmpty()) {
      return Optional.empty();
    }
    List<InlineKeyboardButton> keyboardButtons = availableArmors.stream()
        .map(armor -> new InlineKeyboardButton(armor.toString()).setCallbackData(armor.name()))
        .collect(Collectors.toList());
    return Optional.of(new Communicate(ARMOR, List.of(keyboardButtons)));
  }

  public Optional<Communicate> raceSpecialCommunicate(Race race) {
    if (race.getBuildingRaceTrait() != null) {
      if (BuildingRaceTrait.TWO_ADDITIONAL_ABILITY_POINTS.equals(race.getBuildingRaceTrait())) {
        return extraAbilitiesCommunicate(null, null);
      }
      if (BuildingRaceTrait.WIZARD_CANTRIP.equals(race.getBuildingRaceTrait())) {
        return Optional.of(
            new Communicate(SPELLS, List.of(
                Spell.of(CharacterClass.WIZARD, 0).stream()
                    .map(spell -> new InlineKeyboardButton(spell.toString()).setCallbackData(spell.name()))
                    .collect(Collectors.toList())
            )));
      }
    }
    return Optional.empty();
  }

  public Optional<Communicate> extraAbilitiesCommunicate(Session session, String lastAbility) {
    if (session != null && session.getAbilitiesToImprove().size() == 2) {
      return Optional.empty();
    }
    LinkedList<InlineKeyboardButton> abilities = new LinkedList<>();
    String[] abilitiesNames = {"Str", "Dex", "Con", "Int", "Wis"};
    for (int i = 0; i < 5; ++i) {
      if (lastAbility == null || Integer.valueOf(lastAbility) != i) {
        abilities.add(new InlineKeyboardButton(abilitiesNames[i]).setCallbackData(String.valueOf(i)));
      }
    }
    return Optional.of(new Communicate(EXTRA_ABILITIES, List.of(abilities)));
  }
}
