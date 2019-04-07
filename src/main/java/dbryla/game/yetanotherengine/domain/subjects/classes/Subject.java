package dbryla.game.yetanotherengine.domain.subjects.classes;

import dbryla.game.yetanotherengine.domain.Abilities;
import dbryla.game.yetanotherengine.domain.spells.Effect;
import dbryla.game.yetanotherengine.domain.state.SubjectIdentifier;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Weapon;

import java.util.Optional;

public interface Subject {

  int getInitiativeModifier();

  String getName();

  boolean isTerminated();

  int getHealthPoints();

  int getArmorClass();

  Subject of(int healthPoints);

  Subject of(Effect effect);

  String getAffiliation();

  SubjectIdentifier toIdentifier();

  Weapon getWeapon();

  Optional<ActiveEffect> getActiveEffect();

  Subject effectExpired();

  Abilities getAbilities();
}
