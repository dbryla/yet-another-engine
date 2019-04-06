package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.Instrument;
import dbryla.game.yetanotherengine.domain.subjects.classes.Subject;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Weapon;

import java.util.Set;

public interface Operation {

  Set<Subject> invoke(Subject source, Instrument instrument, Subject... targets) throws UnsupportedGameOperationException;

  int getAllowedNumberOfTargets(Instrument instrument);

}
