package dbryla.game.yetanotherengine.domain.encounters;

import static dbryla.game.yetanotherengine.domain.battleground.Position.ENEMIES_FRONT;
import static dbryla.game.yetanotherengine.domain.subject.Race.GOBLINOID;
import static org.assertj.core.api.Assertions.assertThat;

import dbryla.game.yetanotherengine.domain.subject.Subject;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MonstersFactoryITest {

  @Autowired
  private MonstersFactory monstersFactory;

  @Test
  void shouldCreateMonsterWithRandomHumanoidRace() {
    List<Subject> monsters = monstersFactory.createEncounter(1, 0);

    assertThat(monsters).isNotEmpty();
    assertThat(monsters.get(0).getRace().isPlayable()).isTrue();
  }

  @Test
  void shouldCreateMonsterWithItsRace() {
    List<Subject> monsters = monstersFactory.createEncounter(1, 5);

    assertThat(monsters).isNotEmpty();
    assertThat(monsters.get(0).getRace()).isEqualTo(GOBLINOID);
  }

  @Test
  void shouldCreateMonsterWithPreferredPosition() {
    List<Subject> monsters = monstersFactory.createEncounter(1, 0);

    assertThat(monsters).isNotEmpty();
    assertThat(monsters.get(0).getPosition()).isEqualTo(ENEMIES_FRONT);
  }
}