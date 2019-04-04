package dbryla.game.yetanotherengine.domain;

import dbryla.game.yetanotherengine.domain.subjects.classes.BaseClass;
import dbryla.game.yetanotherengine.domain.subjects.classes.Fighter;
import dbryla.game.yetanotherengine.domain.subjects.classes.Mage;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class GameOptions {

  public static final String PLAYER = "player";
  public static final String ENEMIES = "enemies";


  public Set<Class<? extends BaseClass>> getAvailableClasses() {
    return Set.of(Fighter.class, Mage.class);
  }

}
