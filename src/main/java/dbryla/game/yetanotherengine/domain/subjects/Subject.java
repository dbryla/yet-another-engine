package dbryla.game.yetanotherengine.domain.subjects;

import dbryla.game.yetanotherengine.domain.spells.Effect;
import dbryla.game.yetanotherengine.domain.state.SubjectIdentifier;

import java.util.Set;

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

  int calculateAttackDamage();

  int calculateWeaponHitRoll();

  Weapon getWeapon();

  Effect getActiveEffect();

  int getActiveEffectDurationInTurns();

  void decreaseDurationOfActiveEffect();

  Subject effectExpired();
}
