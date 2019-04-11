package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.Instrument;
import dbryla.game.yetanotherengine.domain.subjects.Subject;

public interface Operation {

  OperationResult invoke(Subject source, Instrument instrument, Subject... targets) throws UnsupportedGameOperationException;

  int getAllowedNumberOfTargets(Instrument instrument);

}
