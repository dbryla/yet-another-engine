package dbryla.game.yetanotherengine.domain.subject;

import dbryla.game.yetanotherengine.domain.game.state.SubjectIdentifier;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.equipment.Equipment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
class SubjectProperties {
  @Getter
  private final SubjectIdentifier id;
  @Getter
  private final Race race;
  @Getter
  private final CharacterClass characterClass;
  @Getter
  private final Equipment equipment;
  @Getter
  private final Abilities abilities;
  @Getter
  private final List<Spell> spells;
  @Getter
  private final int maxHealthPoints;

  int getArmorClass() {
    Integer modifier = abilities.getDexterityModifier();
    if (equipment.getArmor().isPresent()) {
      modifier = equipment.getArmor()
          .get()
          .getMaxDexterityBonus()
          .map(maxDexBonus -> Math.min(maxDexBonus, abilities.getDexterityModifier()))
          .orElse(abilities.getDexterityModifier());
    }
    return equipment.getArmorClass() + modifier;
  }

  public boolean isSpellCaster() {
    return !spells.isEmpty() || characterClass.isSpellCaster();
  }
}