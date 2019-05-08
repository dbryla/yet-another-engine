package dbryla.game.yetanotherengine.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import dbryla.game.yetanotherengine.telegram.Communicate;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Test;

class SessionTest {

  @Test
  void shouldUpdateAbilities() {
    Session session = new Session(null, null);

    session.addAbility("1");

    assertThat(session.getAbilities()).contains(1);
  }

  @Test
  void shouldUpdateAbilitiesSeveralTimes() {
    Session session = new Session(null, null);

    session.addAbility("1");
    session.addAbility("2");
    session.addAbility("3");

    assertThat(session.getAbilities()).contains(1, 2, 3);
  }

  @Test
  void shouldUpdateTargets() {
    Session session = new Session(null, null);

    session.addTarget("target-one");

    assertThat(session.getTargets()).contains("target-one");
  }

  @Test
  void shouldUpdateTargetsSeveralTimes() {
    Session session = new Session(null, null);

    session.addTarget("target-one");
    session.addTarget("target-two");
    session.addTarget("target-three");

    assertThat(session.getTargets()).contains("target-one", "target-two", "target-three");
  }

  @Test
  void shouldUpdateWeapons() {
    Session session = new Session(null, null);

    session.addWeapon("SHORTSWORD");
    
    assertThat(session.getWeapons()).contains(Weapon.SHORTSWORD);
  }

  @Test
  void shouldUpdateWeaponsSeveralTimes() {
    Session session = new Session(null, null);

    session.addWeapon("SHORTSWORD");
    session.addWeapon("SHORTBOW");
    session.addWeapon("LONGBOW");

    assertThat(session.getWeapons()).contains(Weapon.SHORTSWORD, Weapon.SHORTBOW, Weapon.LONGBOW);
  }

  @Test
  void shouldClearTargetsAndSetMovingToFalse() {
    Session session = new Session(null, null);
    session.addTarget("target-one");
    session.setMoving(true);

    session.cleanUpCallbackData();

    assertThat(session.getTargets()).isEmpty();
    assertThat(session.isMoving()).isFalse();
  }

  @Test
  void shouldAddNextCommunicate() {
    Session session = new Session(null, new LinkedList<>(List.of(mock(Communicate.class))), null);
    Communicate communicate = mock(Communicate.class);

    session.addNextCommunicate(communicate);

    assertThat(session.getNextCommunicate()).isEqualTo(communicate);
    assertThat(session.getNextCommunicate()).isNotEqualTo(communicate);
  }

  @Test
  void shouldAddLastCommunicate() {
    Session session = new Session(null, new LinkedList<>(List.of(mock(Communicate.class))), null);
    Communicate communicate = mock(Communicate.class);

    session.addLastCommunicate(communicate);

    assertThat(session.getNextCommunicate()).isNotEqualTo(communicate);
    assertThat(session.getNextCommunicate()).isEqualTo(communicate);
  }

  @Test
  void shouldReturnTrueIfAllTargetsForSpellAreAcquired() {
    Session session = new Session(null, null);
    session.setSpell(Spell.FIRE_BOLT.name());

    session.addTarget("target-one");

    assertThat(session.areAllTargetsAcquired()).isTrue();
  }

  @Test
  void shouldReturnFalseIfAllTargetsForSpellAreNotAcquired() {
    Session session = new Session(null, null);
    session.setSpell(Spell.BLESS.name());

    session.addTarget("target-one");

    assertThat(session.areAllTargetsAcquired()).isFalse();
  }
}