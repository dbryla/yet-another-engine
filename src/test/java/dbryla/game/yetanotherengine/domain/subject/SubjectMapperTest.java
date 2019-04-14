package dbryla.game.yetanotherengine.domain.subject;

import dbryla.game.yetanotherengine.db.PlayerCharacter;
import dbryla.game.yetanotherengine.domain.subject.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SubjectMapperTest {

  private final SubjectMapper subjectMapper = new SubjectMapper();

  @Test
  void shouldCreateSubjectFromPlayerCharacter() {
    PlayerCharacter playerCharacter = PlayerCharacter.builder()
        .name("player")
        .affiliation("blue")
        .characterClass(CharacterClass.WIZARD)
        .race(Race.HIGH_ELF)
        .maxHealthPoints(10)
        .weapon(Weapon.SHORTSWORD)
        .armor(Armor.LEATHER)
        .abilities(new Abilities(10, 10, 10, 10, 10, 10))
        .build();

    Subject subject = subjectMapper.fromCharacter(playerCharacter);

    assertThat(subject.getName()).isEqualTo(playerCharacter.getName());
    assertThat(subject.getAffiliation()).isEqualTo(playerCharacter.getAffiliation());
    assertThat(subject.getCharacterClass()).isEqualTo(playerCharacter.getCharacterClass());
    assertThat(subject.getRace()).isEqualTo(playerCharacter.getRace());
    assertThat(subject.getMaxHealthPoints()).isEqualTo(playerCharacter.getMaxHealthPoints());
    assertThat(subject.getEquipment().getWeapon()).isEqualTo(playerCharacter.getWeapon());
    assertThat(subject.getEquipment().getArmor().get()).isEqualTo(playerCharacter.getArmor());
    assertThat(subject.getAbilities()).isEqualTo(playerCharacter.getAbilities());
  }

  @Test
  void shouldCreatePlayerCharacterFromSubject() {
    Subject subject = new Subject(
        "player",
        Race.HIGH_ELF,
        CharacterClass.WIZARD,
        "blue",
        new Abilities(10, 10, 10, 10, 10, 10),
        Weapon.SHORTSWORD,
        Armor.LEATHER,
        null,
        null,
        10);

    PlayerCharacter playerCharacter = subjectMapper.toCharacter(subject);

    assertThat(subject.getName()).isEqualTo(playerCharacter.getName());
    assertThat(subject.getAffiliation()).isEqualTo(playerCharacter.getAffiliation());
    assertThat(subject.getCharacterClass()).isEqualTo(playerCharacter.getCharacterClass());
    assertThat(subject.getRace()).isEqualTo(playerCharacter.getRace());
    assertThat(subject.getMaxHealthPoints()).isEqualTo(playerCharacter.getMaxHealthPoints());
    assertThat(subject.getEquipment().getWeapon()).isEqualTo(playerCharacter.getWeapon());
    assertThat(subject.getEquipment().getArmor().get()).isEqualTo(playerCharacter.getArmor());
    assertThat(subject.getAbilities()).isEqualTo(playerCharacter.getAbilities());
  }

  @Test
  void shouldCreateNewSubjectWithAdjustedProperties() {
    String name = "player";
    Race race = Race.HIGH_ELF;
    CharacterClass characterClass = CharacterClass.WIZARD;
    String affiliation = "blue";
    Abilities abilities = new Abilities(10, 10, 10, 10, 10, 10);
    Weapon weapon = Weapon.SHORTSWORD;
    Armor armor = Armor.LEATHER;

    Subject subject = subjectMapper.createNewSubject(name,
        race,
        characterClass,
        affiliation,
        abilities,
        weapon,
        armor,
        null,
        null);

    assertThat(subject.getName()).isEqualTo(name);
    assertThat(subject.getAffiliation()).isEqualTo(affiliation);
    assertThat(subject.getCharacterClass()).isEqualTo(characterClass);
    assertThat(subject.getRace()).isEqualTo(race);
    assertThat(subject.getMaxHealthPoints()).isEqualTo(characterClass.getDefaultHealthPoints());
    assertThat(subject.getEquipment().getWeapon()).isEqualTo(weapon);
    assertThat(subject.getEquipment().getArmor().get()).isEqualTo(armor);
    assertThat(subject.getAbilities()).isEqualTo(abilities.of(race.getAbilitiesModifiers()));
  }
}