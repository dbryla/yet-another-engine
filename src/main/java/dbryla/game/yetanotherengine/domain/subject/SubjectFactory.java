package dbryla.game.yetanotherengine.domain.subject;

import static dbryla.game.yetanotherengine.domain.subject.Affiliation.PLAYERS;
import static dbryla.game.yetanotherengine.telegram.BuildingFactory.ABILITIES;
import static dbryla.game.yetanotherengine.telegram.BuildingFactory.ARMOR;
import static dbryla.game.yetanotherengine.telegram.BuildingFactory.CLASS;
import static dbryla.game.yetanotherengine.telegram.BuildingFactory.RACE;
import static dbryla.game.yetanotherengine.telegram.BuildingFactory.WEAPONS;

import dbryla.game.yetanotherengine.db.PlayerCharacter;
import dbryla.game.yetanotherengine.domain.game.state.SubjectIdentifier;
import dbryla.game.yetanotherengine.domain.subject.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subject.equipment.Equipment;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import dbryla.game.yetanotherengine.session.Session;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SubjectFactory {

  public Subject fromCharacter(PlayerCharacter character) {
    SubjectIdentifier id = new SubjectIdentifier(character.getName(), character.getAffiliation());
    Equipment equipment = new Equipment(character.getWeapons(), character.getShield(), character.getArmor());
    CharacterClass characterClass = character.getCharacterClass();
    SubjectProperties subjectProperties = new SubjectProperties(id, character.getRace(), characterClass,
        equipment, character.getAbilities(), character.getSpells(), character.getMaxHealthPoints(), Set.of());
    return new Subject(subjectProperties, characterClass.getPreferredPosition());
  }

  public Subject fromSession(Session session) {
    CharacterClass characterClass = CharacterClass.valueOf((String) session.getData().get(CLASS));
    Race race = Race.valueOf((String) session.getData().get(RACE));
    List<String> abilitiesScores = (List<String>) session.getData().get(ABILITIES);
    Abilities abilities = new Abilities(
        Integer.valueOf(abilitiesScores.get(0)),
        Integer.valueOf(abilitiesScores.get(1)),
        Integer.valueOf(abilitiesScores.get(2)),
        Integer.valueOf(abilitiesScores.get(3)),
        Integer.valueOf(abilitiesScores.get(4)),
        Integer.valueOf(abilitiesScores.get(5)));
    List<Weapon> weapons = ((List<String>) session.getData().get(WEAPONS)).stream().map(Weapon::valueOf).collect(Collectors.toList());
    Armor armor = getArmor(session);
    return createNewSubject(session.getPlayerName(), race, characterClass, PLAYERS,
        abilities, weapons, armor, getShield(characterClass, weapons));
  }

  private Armor getArmor(Session session) {
    String name = (String) session.getData().get(ARMOR);
    if (name != null) {
      return Armor.valueOf(name);
    }
    return null;
  }

  private Armor getShield(CharacterClass characterClass, List<Weapon> weapons) {
    return characterClass.getArmorProficiencies().contains(Armor.SHIELD)
        && weapons.stream().anyMatch(Weapon::isEligibleForShield)
        ? Armor.SHIELD : null;
  }

  public Subject createNewSubject(String playerName, Race race, CharacterClass characterClass, Affiliation affiliation,
      Abilities abilities, List<Weapon> weapons, Armor armor, Armor shield) {
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
        .build();
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
