package dbryla.game.yetanotherengine.telegram;

import static dbryla.game.yetanotherengine.telegram.CommunicateText.ABILITIES;
import static dbryla.game.yetanotherengine.telegram.CommunicateText.ARMOR;
import static dbryla.game.yetanotherengine.telegram.CommunicateText.CLASS;
import static dbryla.game.yetanotherengine.telegram.CommunicateText.EXTRA_ABILITIES;
import static dbryla.game.yetanotherengine.telegram.CommunicateText.RACE;
import static dbryla.game.yetanotherengine.telegram.CommunicateText.SPELLS;
import static dbryla.game.yetanotherengine.telegram.CommunicateText.WEAPONS;
import static org.assertj.core.api.Assertions.assertThat;

import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.CharacterClass;
import dbryla.game.yetanotherengine.domain.subject.Race;
import dbryla.game.yetanotherengine.domain.equipment.Armor;
import dbryla.game.yetanotherengine.domain.equipment.Weapon;
import dbryla.game.yetanotherengine.session.BuildSession;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@SpringBootTest("telegrambots.enabled=false")
@ActiveProfiles("tg")
class BuildingFactoryITest {

  @Autowired
  private BuildingFactory buildingFactory;

  @Test
  void shouldReturnCommunicateWithAvailableClasses() {
    Communicate communicate = buildingFactory.chooseClassCommunicate();

    assertThat(communicate.getText()).isEqualTo(CLASS);
    assertThat(communicate.getKeyboardButtons()).isNotEmpty();
    assertThat(communicate.getKeyboardButtons().get(0))
        .extracting("text", "callbackData")
        .contains(Arrays.stream(CharacterClass.values())
            .map(characterClass -> Tuple.tuple(characterClass.toString(), characterClass.name()))
            .toArray(Tuple[]::new));
  }

  @Test
  void shouldReturnCommunicateWithPlayableRaceGroupsOnly() {
    Communicate communicate = buildingFactory.chooseRaceGroupCommunicate();

    assertThat(communicate.getText()).isEqualTo(RACE);
    List<List<InlineKeyboardButton>> keyboardButtons = communicate.getKeyboardButtons();
    assertThat(keyboardButtons).isNotEmpty();
    keyboardButtons.forEach(row -> assertThat(row.size()).isLessThanOrEqualTo(3));
    assertThat(
        keyboardButtons.stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toList()))
        .extracting("text", "callbackData")
        .contains(
            Arrays.stream(Race.values())
                .filter(Race::isPlayable)
                .map(Race::getDisplayName)
                .distinct()
                .map(race -> Tuple.tuple(race, race.toUpperCase().replace("-", "_")))
                .toArray(Tuple[]::new));
  }

  @Test
  void shouldReturnCommunicateWithPlayableRacesForGivenDisplayName() {
    String displayName = "Elf";
    Communicate communicate = buildingFactory.chooseRaceCommunicate(displayName);

    assertThat(communicate.getText()).isEqualTo(RACE);
    assertThat(communicate.getKeyboardButtons()).isNotEmpty();
    assertThat(communicate.getKeyboardButtons().get(0))
        .extracting("text", "callbackData")
        .contains(
            Arrays.stream(Race.values())
                .filter(race -> displayName.equals(race.getDisplayName()))
                .distinct()
                .map(race -> Tuple.tuple(race.toString(), race.name()))
                .toArray(Tuple[]::new));
  }

  @Test
  void shouldCreateCommunicateWithProvidedScores() {
    List<Integer> scores = List.of(1, 2, 3, 4, 5, 6);

    Communicate communicate = buildingFactory.assignAbilitiesCommunicate(scores);

    assertThat(communicate.getText()).isEqualTo(ABILITIES);
    assertThat(communicate.getKeyboardButtons()).isNotEmpty();
    assertThat(communicate.getKeyboardButtons().get(0))
        .extracting("text", "callbackData")
        .contains(scores.stream()
            .map(score -> Tuple.tuple(score.toString(), score.toString()))
            .toArray(Tuple[]::new));
  }

  @Test
  void shouldReturnNextAbilityAssignmentIfThereIsScoreUnassigned() {
    List<Integer> scores = new LinkedList<>(List.of(1, 2));
    BuildSession session = new BuildSession(null, null, scores);

    Optional<Communicate> communicate = buildingFactory.nextAbilityAssignment(session, "2");

    assertThat(communicate).isPresent();
    assertThat(communicate.get().getText()).isEqualTo(ABILITIES);
    assertThat(communicate.get().getKeyboardButtons()).isNotEmpty();
    assertThat(communicate.get().getKeyboardButtons().get(0))
        .extracting("text", "callbackData")
        .contains(Tuple.tuple("1", "1"));
  }

  @Test
  void shouldNotReturnNextAbilityAssignmentIfAllScoresAreAssigned() {
    List<Integer> scores = new LinkedList<>(List.of(1));
    BuildSession session = new BuildSession(null, null, scores);

    Optional<Communicate> communicate = buildingFactory.nextAbilityAssignment(session, "1");

    assertThat(communicate).isEmpty();
  }

  @Test
  void shouldReturnAvailableWeaponsForHumanFighter() {
    Communicate communicate = buildingFactory.chooseWeaponCommunicate(CharacterClass.FIGHTER, Race.HUMAN);

    assertThat(communicate.getText()).isEqualTo(WEAPONS);
    List<List<InlineKeyboardButton>> keyboardButtons = communicate.getKeyboardButtons();
    assertThat(keyboardButtons).isNotEmpty();
    keyboardButtons.forEach(row -> assertThat(row.size()).isLessThanOrEqualTo(4));
    assertThat(
        keyboardButtons.stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toList()))
        .extracting("text", "callbackData")
        .contains(
            Arrays.stream(Weapon.values())
                .filter(Weapon::isPlayable)
                .map(weapon -> Tuple.tuple(weapon.toString(), weapon.name()))
                .toArray(Tuple[]::new));
  }

  @Test
  void shouldReturnAvailableArmorsForHumanFighter() {
    Optional<Communicate> communicate = buildingFactory.chooseArmorCommunicate(CharacterClass.FIGHTER, Race.HUMAN);

    assertThat(communicate).isPresent();
    assertThat(communicate.get().getText()).isEqualTo(ARMOR);
    assertThat(communicate.get().getKeyboardButtons()).isNotEmpty();
    assertThat(communicate.get().getKeyboardButtons().get(0))
        .extracting("text", "callbackData")
        .contains(Arrays.stream(Armor.values())
            .filter(Armor::isPlayable)
            .filter(armor -> !armor.equals(Armor.SHIELD))
            .map(armor -> Tuple.tuple(armor.toString(), armor.name()))
            .toArray(Tuple[]::new));
  }

  @Test
  void shouldNotReturnSpecialCommunicateForRaceWithoutBuildingTraits() {
    Optional<Communicate> communicate = buildingFactory.raceSpecialCommunicate(Race.HUMAN);

    assertThat(communicate).isEmpty();
  }

  @Test
  void shouldReturnSpecialCommunicateWithSpellsForHighElf() {
    Optional<Communicate> communicate = buildingFactory.raceSpecialCommunicate(Race.HIGH_ELF);

    assertThat(communicate).isPresent();
    assertThat(communicate.get().getText()).isEqualTo(SPELLS);
    assertThat(communicate.get().getKeyboardButtons()).isNotEmpty();
    assertThat(communicate.get().getKeyboardButtons().get(0))
        .extracting("text", "callbackData")
        .contains(Spell.of(CharacterClass.WIZARD, 0)
            .stream()
            .map(spell -> Tuple.tuple(spell.toString(), spell.name()))
            .toArray(Tuple[]::new));
  }

  @Test
  void shouldReturnSpecialCommunicateWithExtraAbilitiesForHalfElf() {
    Optional<Communicate> communicate = buildingFactory.raceSpecialCommunicate(Race.HALF_ELF);

    assertThat(communicate).isPresent();
    assertThat(communicate.get().getText()).isEqualTo(EXTRA_ABILITIES);
    assertThat(communicate.get().getKeyboardButtons()).isNotEmpty();
    assertThat(communicate.get().getKeyboardButtons().get(0))
        .extracting("text")
        .contains("Str", "Dex", "Con", "Int", "Wis");
  }

  @Test
  void shouldReturnExtraAbilitiesCommunicateWithoutDexterity() {
    BuildSession session = new BuildSession(null, null, List.of());
    session.addAbilityToImprove(1);

    Optional<Communicate> communicate = buildingFactory.extraAbilitiesCommunicate(session, "1");

    assertThat(communicate).isPresent();
    assertThat(communicate.get().getText()).isEqualTo(EXTRA_ABILITIES);
    assertThat(communicate.get().getKeyboardButtons()).isNotEmpty();
    assertThat(communicate.get().getKeyboardButtons().get(0))
        .extracting("text")
        .contains("Str", "Con", "Int", "Wis");
  }
}