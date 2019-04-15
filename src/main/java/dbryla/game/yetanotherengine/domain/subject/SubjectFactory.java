package dbryla.game.yetanotherengine.domain.subject;

import dbryla.game.yetanotherengine.db.PlayerCharacter;
import dbryla.game.yetanotherengine.domain.game.state.SubjectIdentifier;
import dbryla.game.yetanotherengine.domain.subject.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subject.equipment.Equipment;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import dbryla.game.yetanotherengine.session.Session;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static dbryla.game.yetanotherengine.domain.game.GameOptions.PLAYERS;
import static dbryla.game.yetanotherengine.telegram.BuildingFactory.*;

@Component
@AllArgsConstructor
public class SubjectFactory {

  public Subject fromCharacter(PlayerCharacter character) {
    SubjectIdentifier id = new SubjectIdentifier(character.getName(), character.getAffiliation());
    Equipment equipment = new Equipment(character.getWeapon(), character.getShield(), character.getArmor());
    CharacterClass characterClass = character.getCharacterClass();
    return new Subject(new SubjectProperties(id, character.getRace(), characterClass,
        equipment, character.getAbilities(), character.getSpells(), character.getMaxHealthPoints()), characterClass.getPreferredPosition());
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
    Weapon weapon = Weapon.valueOf((String) session.getData().get(WEAPON));
    Armor armor = getArmor(session);
    return createNewSubject(session.getPlayerName(), race, characterClass, PLAYERS,
        abilities, weapon, armor, getShield(characterClass, weapon));
  }

  private Armor getArmor(Session session) {
    String name = (String) session.getData().get(ARMOR);
    if (name != null) {
      return Armor.valueOf(name);
    }
    return null;
  }

  private Armor getShield(CharacterClass characterClass, Weapon weapon) {
    return characterClass.getArmorProficiencies().contains(Armor.SHIELD)
        && weapon.isEligibleForShield() ? Armor.SHIELD : null;
  }

  public Subject createNewSubject(String playerName, Race race, CharacterClass characterClass, String affiliation,
                                  Abilities abilities, Weapon weapon, Armor armor, Armor shield) {
    return Subject.builder()
        .name(playerName)
        .race(race)
        .characterClass(characterClass)
        .affiliation(affiliation)
        .abilities(abilities)
        .weapon(weapon)
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
        .weapon(subject.getEquipment().getWeapon())
        .spells(subject.getSpells())
        .build();
  }
}
