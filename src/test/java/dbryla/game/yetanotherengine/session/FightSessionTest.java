package dbryla.game.yetanotherengine.session;

import static org.assertj.core.api.Assertions.assertThat;

import dbryla.game.yetanotherengine.domain.spells.Spell;
import org.junit.jupiter.api.Test;

class FightSessionTest {

  @Test
  void shouldUpdateTargets() {
    FightSession session = new FightSession(null, null);

    session.addTarget("target-one");

    assertThat(session.getTargets()).contains("target-one");
  }

  @Test
  void shouldUpdateTargetsSeveralTimes() {
    FightSession session = new FightSession(null, null);

    session.addTarget("target-one");
    session.addTarget("target-two");
    session.addTarget("target-three");

    assertThat(session.getTargets()).contains("target-one", "target-two", "target-three");
  }

  @Test
  void shouldClearTargetsAndSetMovingToFalse() {
    FightSession session = new FightSession(null, null);
    session.addTarget("target-one");
    session.setMoving(true);

    session.cleanUpCallbackData();

    assertThat(session.getTargets()).isEmpty();
    assertThat(session.isMoving()).isFalse();
  }

  @Test
  void shouldReturnTrueIfAllTargetsForSpellAreAcquired() {
    FightSession session = new FightSession(null, null);
    session.setSpell(Spell.FIRE_BOLT.name());

    session.addTarget("target-one");

    assertThat(session.areAllTargetsAcquired()).isTrue();
  }

  @Test
  void shouldReturnFalseIfAllTargetsForSpellAreNotAcquired() {
    FightSession session = new FightSession(null, null);
    session.setSpell(Spell.BLESS.name());

    session.addTarget("target-one");

    assertThat(session.areAllTargetsAcquired()).isFalse();
  }
}