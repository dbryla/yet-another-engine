package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.subjects.classes.Subject;
import java.util.Set;

public interface Operation<S extends Subject, T extends Subject> {

  Set<Subject> invoke(S source, T... targets) throws UnsupportedGameOperationException;

  int getAllowedNumberOfTargets(S source);

}
