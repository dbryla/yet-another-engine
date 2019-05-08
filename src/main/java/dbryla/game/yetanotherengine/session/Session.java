package dbryla.game.yetanotherengine.session;

import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.CharacterClass;
import dbryla.game.yetanotherengine.domain.subject.Race;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import dbryla.game.yetanotherengine.telegram.Communicate;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Session {

  /**
   * used for regular game
   */
  @Getter
  @Setter
  private Subject subject;
  @Getter
  @Setter
  private boolean spellCasting = false;
  @Getter
  @Setter
  private boolean isMoving = false;
  @Getter
  @Setter
  private boolean isStandingUp = false;
  @Getter
  private String playerName;
  @Getter
  private Weapon weapon;
  @Getter
  private Spell spell;
  @Getter
  private List<String> targets = new LinkedList<>();

  /**
   * used to build character only
   **/
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


  public Session(String playerName, List<Communicate> communicates, List<Integer> abilityScores) {
    this.playerName = playerName;
    this.communicates = communicates;
    this.abilityScores = abilityScores;
  }

  public Session(String playerName, Subject subject) {
    this.playerName = playerName;
    this.subject = subject;
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

  public boolean areAllTargetsAcquired() {
    return spell.getMaximumNumberOfTargets() == targets.size();
  }

  public void cleanUpCallbackData() {
    setMoving(false);
    setStandingUp(false);
    targets.clear();
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

  public void addTarget(String target) {
    targets.add(target);
  }

  public void addWeapon(String weapon) {
    weapons.add(Weapon.valueOf(weapon));
  }

  public void setSpell(String spell) {
    this.spell = Spell.valueOf(spell);
  }

  public void setWeapon(String weapon) {
    this.weapon = Weapon.valueOf(weapon);
  }

  public void addSpell(String spell) {
    spells.add(Spell.valueOf(spell));
  }

  public void setArmor(String armor) {
    this.armor = Armor.valueOf(armor);
  }
}
