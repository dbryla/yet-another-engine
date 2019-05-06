package dbryla.game.yetanotherengine.domain.subject;

import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.effects.Effect;
import dbryla.game.yetanotherengine.domain.encounters.SpecialAttack;
import dbryla.game.yetanotherengine.domain.game.state.SubjectIdentifier;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subject.equipment.Equipment;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
public class Subject {

  @Getter
  private final SubjectProperties subjectProperties;
  @Getter
  private final int currentHealthPoints;
  @Getter
  private final Position position;
  @Getter
  private final Set<Condition> conditions;
  @Getter
  private final Weapon equippedWeapon;

  /**
   * Used to create new subject object with given properties.
   */
  public Subject(SubjectProperties subjectProperties, Position position, Weapon equippedWeapon) {
    this.subjectProperties = subjectProperties;
    this.currentHealthPoints = subjectProperties.getMaxHealthPoints();
    this.position = position;
    this.conditions = new HashSet<>();
    this.equippedWeapon = equippedWeapon;
  }

  public Subject(SubjectProperties subjectProperties, Position position) {
    this(subjectProperties, position, Weapon.FISTS);
  }

  public int getInitiativeModifier() {
    return subjectProperties.getAbilities().getDexterityModifier();
  }

  public String getName() {
    return subjectProperties.getId().getName();
  }

  public boolean isTerminated() {
    return currentHealthPoints <= 0;
  }

  public State getSubjectState() {
    if (currentHealthPoints <= 0) {
      return State.TERMINATED;
    }
    if (currentHealthPoints == subjectProperties.getMaxHealthPoints()) {
      return State.NORMAL;
    }
    if (currentHealthPoints >= Math.ceil(0.75 * subjectProperties.getMaxHealthPoints())) {
      return State.LIGHTLY_WOUNDED;
    }
    if (currentHealthPoints >= Math.ceil(0.50 * subjectProperties.getMaxHealthPoints())) {
      return State.WOUNDED;
    }
    if (currentHealthPoints <= Math.ceil(0.10 * subjectProperties.getMaxHealthPoints()) && currentHealthPoints < 10) {
      return State.DEATHS_DOOR;
    }
    return State.HEAVILY_WOUNDED;
  }

  public int getArmorClass() {
    return subjectProperties.getArmorClass(equippedWeapon);
  }

  public Affiliation getAffiliation() {
    return subjectProperties.getId().getAffiliation();
  }

  public SubjectIdentifier toIdentifier() {
    return subjectProperties.getId();
  }

  public boolean isSpellCaster() {
    return subjectProperties.isSpellCaster();
  }

  public Subject of(int healthPoints) {
    return new Subject(this.subjectProperties, healthPoints, this.position, this.conditions, this.equippedWeapon);
  }

  public Subject of(Condition effect) {
    Set<Condition> effects = new HashSet<>(this.getConditions());
    effects.add(effect);
    return new Subject(this.subjectProperties, this.currentHealthPoints, this.position, effects, this.equippedWeapon);
  }

  public Subject effectExpired(Effect effect) {
    Set<Condition> conditions = new HashSet<>(this.getConditions());
    conditions.removeIf(activeEffect -> activeEffect.getEffect().equals(effect));
    return new Subject(this.subjectProperties, this.currentHealthPoints, this.position, conditions, this.equippedWeapon);
  }

  public Subject of(Position newPosition) {
    if (this.position.equals(newPosition)) {
      return this;
    }
    return new Subject(this.subjectProperties, this.currentHealthPoints, newPosition, this.conditions, this.equippedWeapon);
  }

  public Subject of(Weapon equippedWeapon) {
    return new Subject(this.subjectProperties, this.currentHealthPoints, this.position, this.conditions, equippedWeapon);
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(subjectProperties.getRace() + " " + subjectProperties.getCharacterClass() + "\n"
        + "HP:" + subjectProperties.getMaxHealthPoints() + " AC:" + getArmorClass() + "\n"
        + subjectProperties.getAbilities() + "\n"
        + "Equipment:\n");
    subjectProperties
        .getEquipment()
        .getWeapons()
        .stream()
        .map(Weapon::toString)
        .forEach(weapon -> stringBuilder.append("- ").append(weapon).append("\n"));
    subjectProperties.getEquipment().getArmor().map(Armor::toString).ifPresent(armor -> stringBuilder.append("- ").append(armor).append("\n"));
    subjectProperties.getEquipment().getShield().map(Armor::toString).ifPresent(shield -> stringBuilder.append("- ").append(shield).append("\n"));
    if (subjectProperties.getSpells() != null && !subjectProperties.getSpells().isEmpty()) {
      stringBuilder.append("Additional spells:\n");
      subjectProperties.getSpells().forEach(spell -> stringBuilder.append("- ").append(spell).append("\n"));
    }
    return stringBuilder.toString();
  }

  /**
   * Used to create new subject object, will calculate values for all properties.
   *
   * @return builder for subject object
   */
  public static SubjectBuilder builder() {
    return new SubjectBuilder();
  }

  public List<Spell> getSpells() {
    return subjectProperties.getSpells();
  }

  public Abilities getAbilities() {
    return subjectProperties.getAbilities();
  }

  public int getMaxHealthPoints() {
    return subjectProperties.getMaxHealthPoints();
  }

  public Equipment getEquipment() {
    return subjectProperties.getEquipment();
  }

  public CharacterClass getCharacterClass() {
    return subjectProperties.getCharacterClass();
  }

  public Race getRace() {
    return subjectProperties.getRace();
  }

  public Set<SpecialAttack> getSpecialAttacks() {
    return subjectProperties.getSpecialAttacks();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Subject)) {
      return false;
    }
    Subject subject = (Subject) obj;
    return this.subjectProperties.equals(subject.subjectProperties);
  }

  @Override
  public int hashCode() {
    return this.subjectProperties.hashCode();
  }

  public Set<Enum> getAdvantageOnSavingThrows() {
    return subjectProperties.getAdvantageOnSavingThrows();
  }
}
