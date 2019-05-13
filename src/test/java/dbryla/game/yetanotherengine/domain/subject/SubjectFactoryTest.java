package dbryla.game.yetanotherengine.domain.subject;

import static dbryla.game.yetanotherengine.domain.subject.CharacterClass.FIGHTER;
import static org.assertj.core.api.Assertions.assertThat;

import dbryla.game.yetanotherengine.db.PlayerCharacter;
import dbryla.game.yetanotherengine.domain.equipment.Armor;
import dbryla.game.yetanotherengine.domain.equipment.Equipment;
import dbryla.game.yetanotherengine.domain.equipment.Weapon;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubjectFactoryTest {

  @InjectMocks
  private SubjectFactory subjectFactory;

  @Test
  void shouldCreateSubjectFromPlayerCharacter() {
    PlayerCharacter playerCharacter = PlayerCharacter.builder()
        .name("player")
        .affiliation(Affiliation.PLAYERS)
        .characterClass(FIGHTER)
        .race(Race.HIGH_ELF)
        .maxHealthPoints(10)
        .weapons(List.of(Weapon.SHORTSWORD))
        .armor(Armor.LEATHER)
        .abilities(new Abilities(10, 10, 10, 10, 10, 10))
        .build();

    SubjectProperties subject = subjectFactory.fromCharacter(playerCharacter);

    assertThat(subject.getName()).isEqualTo(playerCharacter.getName());
    assertThat(subject.getAffiliation()).isEqualTo(playerCharacter.getAffiliation());
    assertThat(subject.getCharacterClass()).isEqualTo(playerCharacter.getCharacterClass());
    assertThat(subject.getRace()).isEqualTo(playerCharacter.getRace());
    assertThat(subject.getMaxHealthPoints()).isEqualTo(playerCharacter.getMaxHealthPoints());
    assertThat(subject.getEquipment().getWeapons()).isEqualTo(playerCharacter.getWeapons());
    assertThat(subject.getEquipment().getArmor().get()).isEqualTo(playerCharacter.getArmor());
    assertThat(subject.getAbilities()).isEqualTo(playerCharacter.getAbilities());
  }

  @Test
  void shouldCreatePlayerCharacterFromSubject() {
    SubjectProperties subject = new SubjectProperties(
        "player",
        Affiliation.PLAYERS,
        Race.HIGH_ELF,
        FIGHTER,
        new Equipment(
            List.of(Weapon.SHORTSWORD),
            null,
            Armor.LEATHER),
        new Abilities(10, 10, 10, 10, 10, 10),
        null,
        10,
        Set.of(),
        Set.of());

    PlayerCharacter playerCharacter = subjectFactory.toCharacter(subject);

    assertThat(subject.getName()).isEqualTo(playerCharacter.getName());
    assertThat(subject.getAffiliation()).isEqualTo(playerCharacter.getAffiliation());
    assertThat(subject.getCharacterClass()).isEqualTo(playerCharacter.getCharacterClass());
    assertThat(subject.getRace()).isEqualTo(playerCharacter.getRace());
    assertThat(subject.getMaxHealthPoints()).isEqualTo(playerCharacter.getMaxHealthPoints());
    assertThat(subject.getEquipment().getWeapons()).isEqualTo(playerCharacter.getWeapons());
    assertThat(subject.getEquipment().getArmor().get()).isEqualTo(playerCharacter.getArmor());
    assertThat(subject.getAbilities()).isEqualTo(playerCharacter.getAbilities());
  }

  @Test
  void shouldCreateNewSubjectPropertiesWithAdjustedProperties() {
    String name = "player";
    Race race = Race.HIGH_ELF;
    CharacterClass characterClass = CharacterClass.WIZARD;
    Affiliation affiliation = Affiliation.PLAYERS;
    Abilities abilities = new Abilities(10, 10, 10, 10, 10, 10);
    Weapon weapon = Weapon.SHORTSWORD;
    Armor armor = Armor.LEATHER;

    SubjectProperties subject = subjectFactory.createNewSubjectProperties(name,
        race,
        characterClass,
        affiliation,
        abilities,
        List.of(weapon),
        armor,
        null,
        List.of());

    assertThat(subject.getName()).isEqualTo(name);
    assertThat(subject.getAffiliation()).isEqualTo(affiliation);
    assertThat(subject.getCharacterClass()).isEqualTo(characterClass);
    assertThat(subject.getRace()).isEqualTo(race);
    assertThat(subject.getMaxHealthPoints()).isEqualTo(characterClass.getDefaultHealthPoints());
    assertThat(subject.getEquipment().getWeapons().get(0)).isEqualTo(weapon);
    assertThat(subject.getEquipment().getArmor().get()).isEqualTo(armor);
    assertThat(subject.getAbilities()).isEqualTo(abilities.of(race.getAbilitiesModifiers()));
  }

  @Test
  void shouldCreateNewSubjectWithPreferredClassPosition() {
    SubjectProperties subjectProperties = new SubjectProperties(
        "player",
        Affiliation.PLAYERS,
        Race.HIGH_ELF,
        FIGHTER,
        new Equipment(
            List.of(Weapon.SHORTSWORD),
            null,
            Armor.LEATHER),
        new Abilities(10, 10, 10, 10, 10, 10),
        null,
        10,
        Set.of(),
        Set.of());

    Subject subject = subjectFactory.createNewSubject(subjectProperties);

    assertThat(subject.getPosition()).isEqualTo(FIGHTER.getPreferredPosition());
  }
}