package dbryla.game.yetanotherengine.domain.subject;

import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.effects.Effect;
import dbryla.game.yetanotherengine.domain.encounters.SpecialAttack;
import dbryla.game.yetanotherengine.domain.equipment.Equipment;
import dbryla.game.yetanotherengine.domain.equipment.Weapon;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Set;

import static dbryla.game.yetanotherengine.domain.effects.Effect.*;

@AllArgsConstructor
public class Subject {

  private final SubjectProperties subjectProperties;
  @Getter
  private State state;

  private static final Set<Effect> INCAPACITATED = Set.of(Effect.INCAPACITATED, PARALYZED, PETRIFIED, STUNNED, UNCONSCIOUS);

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

  public Subject of(State state) {
    return new Subject(subjectProperties, state);
  }

  public State withHealthPoints(int healthPoints) {
    State newState = state.of(healthPoints);
    this.state = newState;
    return newState;
  }

  public State withWeapon(Weapon weapon) {
    State newState = state.of(weapon);
    this.state = newState;
    return newState;
  }

  public State newState(Position position) {
    return state.of(position);
  }

  public Subject withoutEffect(Effect effect) {
    this.state = state.effectExpired(effect);
    return this;
  }

  public Subject withCondition(Condition condition) {
    this.state = state.of(condition);
    return this;
  }

  public int getArmorClass() {
    return subjectProperties.getArmorClass(state.getEquippedWeapon());
  }

  public int getInitiativeModifier() {
    return subjectProperties.getAbilities().getDexterityModifier();
  }

  public String getName() {
    return subjectProperties.getName();
  }

  public Weapon getEquippedWeapon() {
    return state.getEquippedWeapon();
  }

  public Affiliation getAffiliation() {
    return subjectProperties.getAffiliation();
  }

  public Abilities getAbilities() {
    return subjectProperties.getAbilities();
  }

  public Race getRace() {
    return subjectProperties.getRace();
  }

  public Set<Condition> getConditions() {
    return state.getConditions();
  }

  public int getCurrentHealthPoints() {
    return state.getCurrentHealthPoints();
  }

  public Set<Enum> getAdvantageOnSavingThrows() {
    return subjectProperties.getAdvantageOnSavingThrows();
  }

  public Set<SpecialAttack> getSpecialAttacks() {
    return subjectProperties.getSpecialAttacks();
  }

  public CharacterClass getCharacterClass() {
    return subjectProperties.getCharacterClass();
  }

  public HealthState getHealthState() {
    return state.getHealthState();
  }

  public Position getPosition() {
    return state.getPosition();
  }

  public Equipment getEquipment() {
    return subjectProperties.getEquipment();
  }

  public List<Spell> getSpells() {
    return subjectProperties.getSpells();
  }

  public int getMaxHealthPoints() {
    return subjectProperties.getMaxHealthPoints();
  }

  public boolean isAbleToMove() {
    return state.getConditions().stream().noneMatch(condition -> PRONE.equals(condition.getEffect()));
  }

  public boolean isAlive() {
    return !state.isTerminated();
  }

  public boolean isTerminated() {
    return state.isTerminated();
  }

  public boolean isSpellCaster() {
    return subjectProperties.isSpellCaster();
  }

  public boolean isRestrained() {
    return state.getConditions().stream().anyMatch(condition -> RESTRAINED.equals(condition.getEffect()));
  }

  public boolean isPetrified() {
    return state.getConditions().stream().anyMatch(condition -> PETRIFIED.equals(condition.getEffect()));
  }

  public boolean cantMove() {
    return state.getConditions().stream().anyMatch(condition -> INCAPACITATED.contains(condition.getEffect()));
  }
}
