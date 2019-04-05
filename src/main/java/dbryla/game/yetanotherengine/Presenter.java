package dbryla.game.yetanotherengine;

import dbryla.game.yetanotherengine.domain.Game;
import dbryla.game.yetanotherengine.domain.operations.Operation;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.domain.subjects.Weapon;
import java.util.Map;

public interface Presenter {

  void showStatus();

  Map<Integer, Class> showAvailableClasses();

  Map<Integer, Weapon> showAvailableWeapons();

  Map<Integer, Spell> showAvailableSpells();

  Map<Integer, Operation> showAvailableOperations(Subject subject);

  Map<Integer, String> showAvailableTargets(Game game);
}
