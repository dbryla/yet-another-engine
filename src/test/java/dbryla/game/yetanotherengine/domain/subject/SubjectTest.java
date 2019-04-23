package dbryla.game.yetanotherengine.domain.subject;

import dbryla.game.yetanotherengine.domain.TestData;
import dbryla.game.yetanotherengine.domain.battleground.Position;
import org.junit.jupiter.api.Test;

import static dbryla.game.yetanotherengine.domain.battleground.Position.PLAYERS_BACK;
import static dbryla.game.yetanotherengine.domain.subject.CharacterClass.CLERIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SubjectTest {

  @Test
  void shouldAddSpellAndAdjustAbilitiesForSubjectBasedOnHighElfTraits() {
    Subject subject = Subject.builder()
        .name("subject")
        .affiliation(Affiliation.PLAYERS)
        .race(Race.HIGH_ELF)
        .abilities(TestData.ABILITIES)
        .healthPoints(1)
        .build();

    assertThat(subject.getSpells()).isNotEmpty();
    assertThat(subject.getAbilities().getDexterityModifier()).isEqualTo(1);
    assertThat(subject.getAbilities().getIntelligenceModifier()).isEqualTo(1);
  }

  @Test
  void shouldAddExtraHealthPointsForSubjectBasedOnHillDwarfTraits() {
    Subject subject = Subject.builder()
        .name("subject")
        .affiliation(Affiliation.PLAYERS)
        .race(Race.HILL_DWARF)
        .characterClass(CLERIC)
        .abilities(TestData.ABILITIES)
        .build();

    assertThat(subject.getMaxHealthPoints()).isEqualTo(CLERIC.getDefaultHealthPoints() + 2);
  }

  @Test
  void shouldMoveTargetToNewPosition() {
    Subject subject = new Subject(mock(SubjectProperties.class), Position.PLAYERS_FRONT);

    Subject movedSubject = subject.of(PLAYERS_BACK);

    assertThat(movedSubject.getPosition()).isEqualTo(PLAYERS_BACK);
  }
}