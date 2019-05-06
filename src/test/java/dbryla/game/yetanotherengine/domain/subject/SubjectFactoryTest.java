package dbryla.game.yetanotherengine.domain.subject;

import dbryla.game.yetanotherengine.db.PlayerCharacter;
import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.game.state.SubjectIdentifier;
import dbryla.game.yetanotherengine.domain.subject.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subject.equipment.Equipment;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static dbryla.game.yetanotherengine.domain.subject.CharacterClass.FIGHTER;
import static org.assertj.core.api.Assertions.assertThat;

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

    Subject subject = subjectFactory.fromCharacter(playerCharacter);

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
  void shouldCreateSubjectFromPlayerCharacterWithPreferredClassPosition() {
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

    Subject subject = subjectFactory.fromCharacter(playerCharacter);

    assertThat(subject.getPosition()).isEqualTo(FIGHTER.getPreferredPosition());
  }

  @Test
  void shouldCreatePlayerCharacterFromSubject() {
    Subject subject = new Subject(
        new SubjectProperties(
            new SubjectIdentifier(
                "player",
                Affiliation.PLAYERS),
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
            Set.of()),
        Position.PLAYERS_FRONT);

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
  void shouldCreateNewSubjectWithAdjustedProperties() {
    String name = "player";
    Race race = Race.HIGH_ELF;
    CharacterClass characterClass = CharacterClass.WIZARD;
    Affiliation affiliation = Affiliation.PLAYERS;
    Abilities abilities = new Abilities(10, 10, 10, 10, 10, 10);
    Weapon weapon = Weapon.SHORTSWORD;
    Armor armor = Armor.LEATHER;

    Subject subject = subjectFactory.createNewSubject(name,
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
    String name = "player";
    Race race = Race.HIGH_ELF;
    CharacterClass characterClass = CharacterClass.WIZARD;
    Affiliation affiliation = Affiliation.PLAYERS;
    Abilities abilities = new Abilities(10, 10, 10, 10, 10, 10);
    Weapon weapon = Weapon.SHORTSWORD;
    Armor armor = Armor.LEATHER;

    Subject subject = subjectFactory.createNewSubject(name,
        race,
        characterClass,
        affiliation,
        abilities,
        List.of(weapon),
        armor,
        null,
        List.of());

    assertThat(subject.getPosition()).isEqualTo(characterClass.getPreferredPosition());
  }
}