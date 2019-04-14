package dbryla.game.yetanotherengine.domain.subject;

import dbryla.game.yetanotherengine.db.PlayerCharacter;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import dbryla.game.yetanotherengine.session.Session;
import org.springframework.stereotype.Component;

import java.util.List;

import static dbryla.game.yetanotherengine.domain.game.GameOptions.PLAYERS;
import static dbryla.game.yetanotherengine.telegram.BuildingFactory.*;
import static dbryla.game.yetanotherengine.telegram.BuildingFactory.ARMOR;

@Component
public class SubjectMapper {

  public Subject fromCharacter(PlayerCharacter character) {
    return new Subject(character.getName(), character.getRace(), character.getCharacterClass(), character.getAffiliation(),
        character.getAbilities(), character.getWeapon(), character.getArmor(), character.getShield(), character.getSpells(),
        character.getMaxHealthPoints());
  }

  public Subject fromSession(Session session) {
    CharacterClass characterClass = CharacterClass.valueOf((String) session.getData().get(CLASS));
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
    Race race = null;
    List<Spell> spells = null;
    return createNewSubject(session.getPlayerName(), race, characterClass, PLAYERS, abilities, weapon, armor, getShield(weapon), spells);
  }

  private Armor getArmor(Session session) {
    String name = (String) session.getData().get(ARMOR);
    if (name != null) {
      return Armor.valueOf(name);
    }
    return null;
  }

  private Armor getShield(Weapon weapon) {
    return weapon.isEligibleForShield() ? Armor.SHIELD : null;
  }

  public Subject createNewSubject(String playerName, Race race, CharacterClass characterClass, String affiliation,
                                  Abilities abilities, Weapon weapon, Armor armor, Armor shield, List<Spell> spells) {
    return Subject.builder()
        .name(playerName)
        .race(race)
        .characterClass(characterClass)
        .affiliation(affiliation)
        .abilities(abilities)
        .weapon(weapon)
        .armor(armor)
        .shield(shield)
        .spells(spells)
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
