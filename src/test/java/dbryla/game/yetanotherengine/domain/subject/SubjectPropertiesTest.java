package dbryla.game.yetanotherengine.domain.subject;

import dbryla.game.yetanotherengine.domain.TestData;
import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dbryla.game.yetanotherengine.domain.battleground.Position.PLAYERS_BACK;
import static dbryla.game.yetanotherengine.domain.subject.CharacterClass.CLERIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SubjectPropertiesTest {

  @Test
  void shouldAdjustAbilitiesForSubjectBasedOnHighElfTraits() {
    SubjectProperties subject = SubjectProperties.builder()
        .name("subject")
        .affiliation(Affiliation.PLAYERS)
        .race(Race.HIGH_ELF)
        .abilities(TestData.ABILITIES)
        .healthPoints(1)
        .build();

    assertThat(subject.getAbilities().getDexterityModifier()).isEqualTo(1);
    assertThat(subject.getAbilities().getIntelligenceModifier()).isEqualTo(1);
  }

  @Test
  void shouldAddSpellForSubject() {
    SubjectProperties subject = SubjectProperties.builder()
        .name("subject")
        .affiliation(Affiliation.PLAYERS)
        .race(Race.HIGH_ELF)
        .abilities(TestData.ABILITIES)
        .spells(List.of(Spell.FIRE_BOLT))
        .healthPoints(1)
        .build();

    assertThat(subject.getSpells()).isNotEmpty();
  }

  @Test
  void shouldAddExtraHealthPointsForSubject() {
    SubjectProperties subject = SubjectProperties.builder()
        .name("subject")
        .affiliation(Affiliation.PLAYERS)
        .race(Race.HUMAN)
        .characterClass(CLERIC)
        .abilities(TestData.ABILITIES)
        .additionalHealthPoints(1)
        .build();

    assertThat(subject.getMaxHealthPoints()).isEqualTo(CLERIC.getDefaultHealthPoints() + 1);
  }

}