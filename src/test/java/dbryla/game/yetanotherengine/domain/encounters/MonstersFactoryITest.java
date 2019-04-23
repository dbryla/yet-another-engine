package dbryla.game.yetanotherengine.domain.encounters;

import dbryla.game.yetanotherengine.domain.subject.Subject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static dbryla.game.yetanotherengine.domain.battleground.Position.ENEMIES_FRONT;
import static dbryla.game.yetanotherengine.domain.subject.Race.GOBLINOID;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MonstersFactoryITest {

  @Autowired
  private MonstersFactory monstersFactory;

  @Autowired
  private MonstersBook monstersBook;

  @Test
  void shouldCreateMonsterWithRandomHumanoidRace() {
    List<Subject> monsters = monstersFactory.createEncounter(1, 0);

    assertThat(monsters).isNotEmpty();
    assertThat(monsters.get(0).getRace().isPlayable()).isTrue();
  }

  @Test
  void shouldCreateMonsterWithItsRace() {
    List<Subject> monsters = monstersFactory.createEncounter(1, goblinsNumber());

    assertThat(monsters).isNotEmpty();
    assertThat(monsters.get(0).getRace()).isEqualTo(GOBLINOID);
  }

  private int goblinsNumber() {
    List<MonsterDefinition> monsters = monstersBook.getMonsters();
    for (int i = 0; i < monsters.size(); i++) {
      if (monsters.get(i).getDefaultName().equals("Goblin")) {
        return i;
      }
    }
    return -1;
  }

  @Test
  void shouldCreateMonsterWithPreferredPosition() {
    List<Subject> monsters = monstersFactory.createEncounter(1, 0);

    assertThat(monsters).isNotEmpty();
    assertThat(monsters.get(0).getPosition()).isEqualTo(ENEMIES_FRONT);
  }
}