package dbryla.game.yetanotherengine.domain.subjects;

import dbryla.game.yetanotherengine.domain.spells.Effect;
import dbryla.game.yetanotherengine.domain.state.SubjectIdentifier;
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

  Optional<Effect> getActiveEffect();

  int getActiveEffectDurationInTurns();

  void decreaseDurationOfActiveEffect();

  Subject effectExpired();
}
