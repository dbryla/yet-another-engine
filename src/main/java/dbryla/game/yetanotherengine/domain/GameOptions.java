package dbryla.game.yetanotherengine.domain;

import dbryla.game.yetanotherengine.domain.subjects.classes.Cleric;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subjects.equipment.Weapon;
import dbryla.game.yetanotherengine.domain.subjects.classes.BaseClass;
import dbryla.game.yetanotherengine.domain.subjects.classes.Fighter;
import dbryla.game.yetanotherengine.domain.subjects.classes.Wizard;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class GameOptions {

  public static final String ALLIES = "player";
  public static final String ENEMIES = "enemies";
  private static final Set<Class> AVAILABLE_CLASSES = Set.of(Fighter.class, Wizard.class, Cleric.class);
  private static final Set<Class> SPELL_CASTERS = Set.of(Wizard.class, Cleric.class);

  public Set<Class> getAvailableClasses() {
    return AVAILABLE_CLASSES;
  }

  public Set<Weapon> getAvailableWeapons(Class clazz) {
    if (Fighter.class.equals(clazz)) {
      return Set.of(Weapon.values());
    }
    if (Wizard.class.equals(clazz)) {
      return Set.of(Weapon.DAGGER, Weapon.QUARTERSTAFF);
    }
    if (Cleric.class.equals(clazz)) {
      return Arrays.stream(Weapon.values()).filter(Weapon::isSimpleType).collect(Collectors.toSet());
    }
    return Set.of();
  }

  public Set<Armor> getAvailableArmors(Class clazz) {
    if (Fighter.class.equals(clazz)) {
      Set<Armor> armors = new HashSet<>(Set.of(Armor.values()));
      armors.remove(Armor.SHIELD);
      return armors;
    }
    if (Cleric.class.equals(clazz)) {
      return Arrays.stream(Armor.values()).filter(Armor::isNotHeavyArmor).collect(Collectors.toSet());
    }
    return Set.of();
  }

  public boolean isSpellCaster(Class clazz) {
    return SPELL_CASTERS.contains(clazz);
  }
}
