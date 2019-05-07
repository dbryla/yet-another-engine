package dbryla.game.yetanotherengine.domain.subject;

import dbryla.game.yetanotherengine.db.PlayerCharacter;
import dbryla.game.yetanotherengine.domain.game.state.SubjectIdentifier;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subject.equipment.Equipment;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import dbryla.game.yetanotherengine.session.Session;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static dbryla.game.yetanotherengine.domain.subject.Affiliation.PLAYERS;
import static dbryla.game.yetanotherengine.telegram.CommunicateText.*;

@Component
@AllArgsConstructor
public class SubjectFactory {

  public Subject fromCharacter(PlayerCharacter character) {
    SubjectIdentifier id = new SubjectIdentifier(character.getName(), character.getAffiliation());
    Equipment equipment = new Equipment(character.getWeapons(), character.getShield(), character.getArmor());
    CharacterClass characterClass = character.getCharacterClass();
    SubjectProperties subjectProperties = new SubjectProperties(id, character.getRace(), characterClass,
        equipment, character.getAbilities(), character.getSpells(), character.getMaxHealthPoints(), Set.of(), Set.of());
    return new Subject(subjectProperties, characterClass.getPreferredPosition());
  }

  public Subject fromSession(Session session) {
    CharacterClass characterClass = CharacterClass.valueOf((String) session.getGenericData().get(CLASS));
    Race race = Race.valueOf((String) session.getGenericData().get(RACE));
    List<String> abilitiesScores = session.listOf(ABILITIES);
    Abilities abilities = new Abilities(
        Integer.valueOf(abilitiesScores.get(0)),
        Integer.valueOf(abilitiesScores.get(1)),
        Integer.valueOf(abilitiesScores.get(2)),
        Integer.valueOf(abilitiesScores.get(3)),
        Integer.valueOf(abilitiesScores.get(4)),
        Integer.valueOf(abilitiesScores.get(5)));
    abilities = modifyAbilitiesIfApplicable(session, abilities);
    List<Weapon> weapons = session.listOf(WEAPONS).stream().map(Weapon::valueOf).collect(Collectors.toList());
    Armor armor = getArmor(session);
    Optional<Spell> spell = getSpell(session);
    return createNewSubject(session.getPlayerName(), race, characterClass, PLAYERS,
        abilities, weapons, armor, getShield(characterClass, weapons), spell.map(List::of).orElse(List.of()));
  }

  private Abilities modifyAbilitiesIfApplicable(Session session, Abilities abilities) {
    List<String> abilitiesScoresModifiers = session.listOf(EXTRA_ABILITIES);
    if (abilitiesScoresModifiers == null || abilitiesScoresModifiers.isEmpty()) {
      return abilities;
    }
    abilities = abilities.of(Integer.valueOf(abilitiesScoresModifiers.get(0)), Integer.valueOf(abilitiesScoresModifiers.get(1)));
    return abilities;
  }

  private Armor getArmor(Session session) {
    String name = (String) session.getGenericData().get(ARMOR);
    if (name != null) {
      return Armor.valueOf(name);
    }
    return null;
  }

  private Optional<Spell> getSpell(Session session) {
    try {
      String spellName = (String) session.getGenericData().get(SPELLS);
      if (spellName != null) {
        return Optional.of(Spell.valueOf(spellName));
      }
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
    return Optional.empty();
  }

  private Armor getShield(CharacterClass characterClass, List<Weapon> weapons) {
    return characterClass.getArmorProficiencies().contains(Armor.SHIELD)
        && weapons.stream().anyMatch(Weapon::isEligibleForShield)
        ? Armor.SHIELD : null;
  }

  public Subject createNewSubject(String playerName, Race race, CharacterClass characterClass, Affiliation affiliation,
                                  Abilities abilities, List<Weapon> weapons, Armor armor, Armor shield, List<Spell> spells) {
    return Subject.builder()
        .name(playerName)
        .race(race)
        .characterClass(characterClass)
        .affiliation(affiliation)
        .abilities(abilities)
        .weapons(weapons)
        .armor(armor)
        .shield(shield)
        .position(characterClass.getPreferredPosition())
        .spells(spells)
        .additionalHealthPoints(shouldGetAdditionalHealthPoint(race) ? 1 : 0)
        .build();
  }

  private boolean shouldGetAdditionalHealthPoint(Race race) {
    return race.getBuildingRaceTrait() != null && BuildingRaceTrait.ADDITIONAL_HEALTH_POINT.equals(race.getBuildingRaceTrait());
  }

  public PlayerCharacter toCharacter(Subject subject) {
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
