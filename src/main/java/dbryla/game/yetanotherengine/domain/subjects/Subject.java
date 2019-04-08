package dbryla.game.yetanotherengine.domain.subjects;

import dbryla.game.yetanotherengine.domain.Abilities;
import dbryla.game.yetanotherengine.domain.spells.Effect;
import dbryla.game.yetanotherengine.domain.state.SubjectIdentifier;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Equipment;
import java.util.Optional;

public interface Subject {

  int getInitiativeModifier();

  String getName();

  boolean isTerminated();

  State getSubjectState();

  int getCurrentHealthPoints();

  int getArmorClass();

  Subject of(int healthPoints);

  Subject of(Effect effect);

  String getAffiliation();

  SubjectIdentifier toIdentifier();

  Optional<ActiveEffect> getActiveEffect();

  Subject effectExpired();

  Abilities getAbilities();

  Equipment getEquipment();
}
