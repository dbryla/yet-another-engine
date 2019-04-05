package dbryla.game.yetanotherengine;

import dbryla.game.yetanotherengine.domain.Action;
import dbryla.game.yetanotherengine.domain.Game;
import dbryla.game.yetanotherengine.domain.subjects.Subject;

public interface InputProvider {

  Action askForAction(Subject subject, Game game);
}
