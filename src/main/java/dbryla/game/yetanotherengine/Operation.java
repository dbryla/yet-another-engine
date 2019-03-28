package dbryla.game.yetanotherengine;

import java.util.Set;

public interface Operation<S extends Subject, T extends Subject> {

  Set<Subject> invoke(S source, T... targets) throws UnsupportedGameOperationException;

}
