package dbryla.game.yetanotherengine.domain.subject;

import static dbryla.game.yetanotherengine.domain.subject.Affiliation.PLAYERS;

import dbryla.game.yetanotherengine.db.PlayerCharacter;
import dbryla.game.yetanotherengine.domain.equipment.Armor;
import dbryla.game.yetanotherengine.domain.equipment.Equipment;
import dbryla.game.yetanotherengine.domain.equipment.Weapon;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.session.BuildSession;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SubjectFactory {

  public SubjectProperties fromCharacter(PlayerCharacter character) {
    Equipment equipment = new Equipment(character.getWeapons(), character.getShield(), character.getArmor());
    CharacterClass characterClass = character.getCharacterClass();
    return new SubjectProperties(character.getName(), character.getAffiliation(), character.getRace(), characterClass,
        equipment, character.getAbilities(), character.getSpells(), character.getMaxHealthPoints(), Set.of(), Set.of());
  }

  public SubjectProperties fromSession(BuildSession session) {
    List<Integer> abilitiesScores = session.getAbilities();
    Abilities abilities = new Abilities(
        abilitiesScores.get(0),
        abilitiesScores.get(1),
        abilitiesScores.get(2),
        abilitiesScores.get(3),
        abilitiesScores.get(4),
        abilitiesScores.get(5));
    abilities = modifyAbilitiesIfApplicable(session, abilities);
    List<Weapon> weapons = session.getWeapons();

    CharacterClass characterClass = session.getCharacterClass();
    return createNewSubjectProperties(session.getPlayerName(), session.getRace(), characterClass, PLAYERS,
        abilities, weapons, session.getArmor(), getShield(characterClass, weapons), session.getSpells());
  }

  private Abilities modifyAbilitiesIfApplicable(BuildSession session, Abilities abilities) {
    List<Integer> abilitiesScoresModifiers = session.getAbilitiesToImprove();
    if (abilitiesScoresModifiers.isEmpty()) {
      return abilities;
    }
    abilities = abilities.of(abilitiesScoresModifiers.get(0), abilitiesScoresModifiers.get(1));
    return abilities;
  }


  private Armor getShield(CharacterClass characterClass, List<Weapon> weapons) {
    return characterClass.getArmorProficiencies().contains(Armor.SHIELD)
        && weapons.stream().anyMatch(Weapon::isEligibleForShield)
        ? Armor.SHIELD : null;
  }

  public Subject createNewSubject(SubjectProperties subjectProperties) {
    return new Subject(subjectProperties,
        new State(subjectProperties.getName(), subjectProperties.getMaxHealthPoints(), subjectProperties.getMaxHealthPoints(),
            subjectProperties.getCharacterClass().getPreferredPosition(), Set.of(), Weapon.FISTS));
  }

  public SubjectProperties createNewSubjectProperties(String playerName, Race race, CharacterClass characterClass, Affiliation affiliation,
      Abilities abilities, List<Weapon> weapons, Armor armor, Armor shield, List<Spell> spells) {
    return SubjectProperties.builder()
        .name(playerName)
        .race(race)
        .characterClass(characterClass)
        .affiliation(affiliation)
        .abilities(abilities)
        .weapons(weapons)
        .armor(armor)
        .shield(shield)
        .spells(spells)
        .additionalHealthPoints(shouldGetAdditionalHealthPoint(race) ? 1 : 0)
        .build();
  }

  private boolean shouldGetAdditionalHealthPoint(Race race) {
    return race.getBuildingRaceTrait() != null && BuildingRaceTrait.ADDITIONAL_HEALTH_POINT.equals(race.getBuildingRaceTrait());
  }

  public PlayerCharacter toCharacter(SubjectProperties subject) {
    return PlayerCharacter.builder()
        .name(subject.getName())
        .affiliation(subject.getAffiliation())
        .abilities(subject.getAbilities())
        .characterClass(subject.getCharacterClass())
        .race(subject.getRace())
        .maxHealthPoints(subject.getMaxHealthPoints())
        .armor(subject.getEquipment().getArmor().orElse(null))
        .shield(subject.getEquipment().getShield().orElse(null))
        .weapons(subject.getEquipment().getWeapons())
        .spells(subject.getSpells())
        .build();
  }
}
