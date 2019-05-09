package dbryla.game.yetanotherengine.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import dbryla.game.yetanotherengine.telegram.Communicate;
import dbryla.game.yetanotherengine.telegram.commands.CommandTestSetup;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Test;

class BuildSessionTest {

  @Test
  void shouldUpdateAbilities() {
    BuildSession session = new BuildSession(null, null, List.of());

    session.addAbility("1");

    assertThat(session.getAbilities()).contains(1);
  }

  @Test
  void shouldUpdateAbilitiesSeveralTimes() {
    BuildSession session = new BuildSession(null, null, List.of());

    session.addAbility("1");
    session.addAbility("2");
    session.addAbility("3");

    assertThat(session.getAbilities()).contains(1, 2, 3);
  }

  @Test
  void shouldUpdateWeapons() {
    BuildSession session = new BuildSession(null, null, List.of());

    session.addWeapon("SHORTSWORD");

    assertThat(session.getWeapons()).contains(Weapon.SHORTSWORD);
  }

  @Test
  void shouldUpdateWeaponsSeveralTimes() {
    BuildSession session = new BuildSession(null, null, List.of());

    session.addWeapon("SHORTSWORD");
    session.addWeapon("SHORTBOW");
    session.addWeapon("LONGBOW");

    assertThat(session.getWeapons()).contains(Weapon.SHORTSWORD, Weapon.SHORTBOW, Weapon.LONGBOW);
  }

  @Test
  void shouldAddNextCommunicate() {
    BuildSession session = new BuildSession(null, new LinkedList<>(), List.of());
    Communicate communicate = mock(Communicate.class);

    session.addNextCommunicate(communicate);

    assertThat(session.getNextCommunicate()).isEqualTo(communicate);
    assertThat(session.getNextCommunicate()).isNotEqualTo(communicate);
  }

  @Test
  void shouldAddLastCommunicate() {
    BuildSession session = new BuildSession(null, new LinkedList<>(List.of(mock(Communicate.class))), List.of());
    Communicate communicate = mock(Communicate.class);

    session.addLastCommunicate(communicate);

    assertThat(session.getNextCommunicate()).isNotEqualTo(communicate);
    assertThat(session.getNextCommunicate()).isEqualTo(communicate);
  }

}