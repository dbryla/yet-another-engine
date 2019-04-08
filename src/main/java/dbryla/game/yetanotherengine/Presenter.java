package dbryla.game.yetanotherengine;

import dbryla.game.yetanotherengine.domain.Game;
import dbryla.game.yetanotherengine.domain.operations.Operation;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Weapon;
import java.util.List;

public interface Presenter {

  void showStatus();

  List<Class> showAvailableClasses();

  List<Weapon> showAvailableWeapons(Class clazz);

  List<Spell> showAvailableSpells(Class clazz);

  List<Operation> showAvailableOperations(Class clazz);

  List<String> showAvailableEnemyTargets(Game game);

  List<String> showAvailableFriendlyTargets(Game game);

  List<Armor> showAvailableShield();

  List<Armor> showAvailableArmors(Class clazz);

  List<Integer> showGeneratedAbilityScores();
}
