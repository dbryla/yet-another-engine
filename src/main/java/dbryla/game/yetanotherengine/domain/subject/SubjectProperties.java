package dbryla.game.yetanotherengine.domain.subject;

import dbryla.game.yetanotherengine.domain.game.state.SubjectIdentifier;
import dbryla.game.yetanotherengine.domain.encounters.SpecialAttack;
import dbryla.game.yetanotherengine.domain.operations.DamageType;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.equipment.Equipment;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Getter
class SubjectProperties {

  private final SubjectIdentifier id;
  private final Race race;
  private final CharacterClass characterClass;
  private final Equipment equipment;
  private final Abilities abilities;
  private final List<Spell> spells;
  private final int maxHealthPoints;
  private final Set<SpecialAttack> specialAttacks;
  private final Set<DamageType> immunities;

  int getArmorClass(Weapon equippedWeapon) {
    Integer modifier = abilities.getDexterityModifier();
    if (equipment.getArmor().isPresent()) {
      modifier = equipment.getArmor()
          .get()
          .getMaxDexterityBonus()
          .map(maxDexBonus -> Math.min(maxDexBonus, abilities.getDexterityModifier()))
          .orElse(abilities.getDexterityModifier());
    }
    return equipment.getArmorClass(equippedWeapon) + modifier;
  }

  boolean isSpellCaster() {
    return !spells.isEmpty() || characterClass.isSpellCaster();
  }
}