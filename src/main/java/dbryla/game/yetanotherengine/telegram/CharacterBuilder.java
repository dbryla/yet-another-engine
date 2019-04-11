package dbryla.game.yetanotherengine.telegram;

import static dbryla.game.yetanotherengine.domain.GameOptions.ALLIES;
import static dbryla.game.yetanotherengine.telegram.CommunicateFactory.ABILITIES;
import static dbryla.game.yetanotherengine.telegram.CommunicateFactory.ARMOR;
import static dbryla.game.yetanotherengine.telegram.CommunicateFactory.CLASS;
import static dbryla.game.yetanotherengine.telegram.CommunicateFactory.WEAPON;

import dbryla.game.yetanotherengine.domain.Abilities;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.domain.subjects.classes.Cleric;
import dbryla.game.yetanotherengine.domain.subjects.classes.Fighter;
import dbryla.game.yetanotherengine.domain.subjects.classes.Wizard;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Weapon;
import dbryla.game.yetanotherengine.session.Session;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CharacterBuilder {

  public Subject create(Session session) {
    String className = (String) session.getData().get(CLASS);
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
    if (Fighter.class.getSimpleName().equals(className)) {
      return Fighter.builder()
          .name(session.getPlayerName())
          .affiliation(ALLIES)
          .abilities(abilities)
          .weapon(weapon)
          .armor(armor)
          .shield(weapon.isEligibleForShield() ? Armor.SHIELD : null)
          .build();
    }
    if (Wizard.class.getSimpleName().equals(className)) {
      return Wizard.builder()
          .name(session.getPlayerName())
          .affiliation(ALLIES)
          .abilities(abilities)
          .weapon(weapon)
          .build();
    }
    if (Cleric.class.getSimpleName().equals(className)) {
      return Cleric.builder()
          .name(session.getPlayerName())
          .affiliation(ALLIES)
          .abilities(abilities)
          .weapon(weapon)
          .armor(armor)
          .shield(weapon.isEligibleForShield() ? Armor.SHIELD : null)
          .build();
    }
    throw new IllegalArgumentException("Unsupported class: " + className);
  }

  private Armor getArmor(Session session) {
    String name = (String) session.getData().get(ARMOR);
    if (name != null) {
       return Armor.valueOf(name);
    }
    return null;
  }
}
