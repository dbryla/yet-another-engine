package dbryla.game.yetanotherengine.session;

import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.CharacterClass;
import dbryla.game.yetanotherengine.domain.subject.Race;
import dbryla.game.yetanotherengine.domain.subject.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import dbryla.game.yetanotherengine.telegram.Communicate;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class BuildSession {

  @Getter
  private String playerName;
  @Getter
  private CharacterClass characterClass;
  @Getter
  private List<Integer> abilityScores;
  @Setter
  @Getter
  private Race race;
  @Getter
  private List<Integer> abilities = new LinkedList<>();
  @Getter
  private List<Integer> abilitiesToImprove = new LinkedList<>();
  @Getter
  private List<Weapon> weapons = new LinkedList<>();
  @Getter
  private List<Spell> spells = new LinkedList<>();
  @Getter
  private Armor armor;
  private List<Communicate> communicates;

  public BuildSession(String playerName, List<Communicate> communicates, List<Integer> abilityScores) {
    this.playerName = playerName;
    this.communicates = communicates;
    this.abilityScores = abilityScores;
  }

  public Communicate getNextCommunicate() {
    if (communicates.isEmpty()) {
      return null;
    }
    return communicates.remove(0);
  }

  public void addNextCommunicate(Communicate communicate) {
    communicates.add(0, communicate);
  }

  public void addLastCommunicate(Communicate communicate) {
    communicates.add(communicate);
  }

  public void setCharacterClass(String characterClass) {
    this.characterClass = CharacterClass.valueOf(characterClass);
  }

  public void addAbility(String ability) {
    abilities.add(Integer.valueOf(ability));
  }

  public void addAbilityToImprove(int abilityIndex) {
    abilitiesToImprove.add(abilityIndex);
  }

  public void addWeapon(String weapon) {
    weapons.add(Weapon.valueOf(weapon));
  }

  public void addSpell(String spell) {
    spells.add(Spell.valueOf(spell));
  }

  public void setArmor(String armor) {
    this.armor = Armor.valueOf(armor);
  }

}
