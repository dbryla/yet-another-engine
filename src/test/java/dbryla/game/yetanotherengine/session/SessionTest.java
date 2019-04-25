package dbryla.game.yetanotherengine.session;

import static dbryla.game.yetanotherengine.telegram.BuildingFactory.ABILITIES;
import static dbryla.game.yetanotherengine.telegram.BuildingFactory.WEAPONS;
import static dbryla.game.yetanotherengine.telegram.FightFactory.SPELL;
import static dbryla.game.yetanotherengine.telegram.FightFactory.TARGETS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.telegram.Communicate;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Test;

class SessionTest {

  @Test
  void shouldUpdateAbilities() {
    Session session = new Session(null, null);

    session.update(ABILITIES, "1");

    assertThat(((List) session.getData().get(ABILITIES))).contains("1");
  }

  @Test
  void shouldUpdateAbilitiesSeveralTimes() {
    Session session = new Session(null, null);

    session.update(ABILITIES, "1");
    session.update(ABILITIES, "2");
    session.update(ABILITIES, "3");

    assertThat(((List) session.getData().get(ABILITIES))).contains("1", "2", "3");
  }

  @Test
  void shouldUpdateTargets() {
    Session session = new Session(null, null);

    session.update(TARGETS, "target-one");

    assertThat(session.getTargets()).contains("target-one");
  }

  @Test
  void shouldUpdateTargetsSeveralTimes() {
    Session session = new Session(null, null);

    session.update(TARGETS, "target-one");
    session.update(TARGETS, "target-two");
    session.update(TARGETS, "target-three");

    assertThat(session.getTargets()).contains("target-one", "target-two", "target-three");
  }

  @Test
  void shouldUpdateWeapons() {
    Session session = new Session(null, null);

    session.update(WEAPONS, "SHORTSWORD");

    assertThat(session.getData().get(WEAPONS)).isNotNull();
    assertThat(((List) session.getData().get(WEAPONS))).contains("SHORTSWORD");
  }

  @Test
  void shouldUpdateWeaponsSeveralTimes() {
    Session session = new Session(null, null);

    session.update(WEAPONS, "SHORTSWORD");
    session.update(WEAPONS, "SHORTBOW");
    session.update(WEAPONS, "LONGBOW");

    assertThat(session.getData().get(WEAPONS)).isNotNull();
    assertThat(((List) session.getData().get(WEAPONS))).contains("SHORTSWORD", "SHORTBOW", "LONGBOW");
  }

  @Test
  void shouldClearTargetsAndSetMovingToFalse() {
    Session session = new Session(null, null);
    session.update(TARGETS, "target-one");
    session.setMoving(true);

    session.cleanUpCallbackData();

    assertThat(session.getTargets()).isEmpty();
    assertThat(session.isMoving()).isFalse();
  }

  @Test
  void shouldAddNextCommunicate() {
    Session session = new Session(null,new LinkedList<>(List.of(mock(Communicate.class))), null);
    Communicate communicate = mock(Communicate.class);

    session.addNextCommunicate(communicate);

    assertThat(session.getNextCommunicate().get()).isEqualTo(communicate);
    assertThat(session.getNextCommunicate().get()).isNotEqualTo(communicate);
  }

  @Test
  void shouldAddLastCommunicate() {
    Session session = new Session(null,new LinkedList<>(List.of(mock(Communicate.class))), null);
    Communicate communicate = mock(Communicate.class);

    session.addLastCommunicate(communicate);

    assertThat(session.getNextCommunicate().get()).isNotEqualTo(communicate);
    assertThat(session.getNextCommunicate().get()).isEqualTo(communicate);
  }

  @Test
  void shouldReturnTrueIfAllTargetsForSpellAreAcquired() {
    Session session = new Session(null, null);
    session.update(SPELL, Spell.FIRE_BOLT.name());

    session.update(TARGETS, "target-one");

    assertThat(session.areAllTargetsAcquired()).isTrue();
  }

  @Test
  void shouldReturnFalseIfAllTargetsForSpellAreNotAcquired() {
    Session session = new Session(null, null);
    session.update(SPELL, Spell.BLESS.name());

    session.update(TARGETS, "target-one");

    assertThat(session.areAllTargetsAcquired()).isFalse();
  }
}