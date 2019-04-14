package dbryla.game.yetanotherengine.domain.subject;

import org.junit.jupiter.api.Test;

import static dbryla.game.yetanotherengine.domain.subject.CharacterClass.CLERIC;
import static org.assertj.core.api.Assertions.assertThat;

class SubjectTest {

  @Test
  void shouldAddSpellAndAdjustAbilitiesForSubjectBasedOnHighElfTraits() {
    Subject subject = Subject.builder()
        .name("subject")
        .affiliation("blue")
        .race(Race.HIGH_ELF)
        .abilities(new Abilities(10, 10, 10, 11, 10, 10))
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
        .affiliation("blue")
        .race(Race.HILL_DWARF)
        .characterClass(CLERIC)
        .abilities(new Abilities(10, 10, 10, 11, 10, 10))
        .build();

    assertThat(subject.getMaxHealthPoints()).isEqualTo(CLERIC.getDefaultHealthPoints() + 1);
  }
}