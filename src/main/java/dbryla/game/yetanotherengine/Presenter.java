package dbryla.game.yetanotherengine;

import dbryla.game.yetanotherengine.domain.Game;
import dbryla.game.yetanotherengine.domain.operations.Operation;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.domain.subjects.Weapon;
import java.util.List;

public interface Presenter {

  void showStatus();

  List<Class> showAvailableClasses();

  List<Weapon> showAvailableWeapons();

  List<Spell> showAvailableSpells();

  List<Operation> showAvailableOperations(Subject subject);

  List<String> showAvailableTargets(Game game);
}
