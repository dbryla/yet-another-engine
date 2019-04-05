package dbryla.game.yetanotherengine.domain;

import dbryla.game.yetanotherengine.domain.subjects.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Weapon;
import dbryla.game.yetanotherengine.domain.subjects.classes.BaseClass;
import dbryla.game.yetanotherengine.domain.subjects.classes.Fighter;
import dbryla.game.yetanotherengine.domain.subjects.classes.Mage;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class GameOptions {

  public static final String PLAYER = "player";
  public static final String ENEMIES = "enemies";

  public Set<Class<? extends BaseClass>> getAvailableClasses() {
    return Set.of(Fighter.class, Mage.class);
  }

  public Set<Weapon> getAvailableWeapons(Class clazz) {
    if (Fighter.class.equals(clazz)) {
      return Set.of(Weapon.values());
    } else if (Mage.class.equals(clazz)) {
      return Set.of(Weapon.DAGGER, Weapon.QUARTERSTAFF);
    }
    return Set.of();
  }

  public Set<Armor> getAvailableArmors(Class clazz) {
    if (Fighter.class.equals(clazz)) {
      Set<Armor> armors = new HashSet<>(Set.of(Armor.values()));
      armors.remove(Armor.SHIELD);
      return armors;
    }
    return Set.of();
  }
}
