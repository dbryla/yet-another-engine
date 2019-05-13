package dbryla.game.yetanotherengine.domain.subject;

import dbryla.game.yetanotherengine.domain.encounters.SpecialAttack;
import dbryla.game.yetanotherengine.domain.equipment.Armor;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.equipment.Equipment;
import dbryla.game.yetanotherengine.domain.equipment.Weapon;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Getter
public class SubjectProperties {

  private final String name;
  private final Affiliation affiliation;
  private final Race race;
  private final CharacterClass characterClass;
  private final Equipment equipment;
  private final Abilities abilities;
  private final List<Spell> spells;
  private final int maxHealthPoints;
  private final Set<SpecialAttack> specialAttacks;
  private final Set<? extends Enum> advantageOnSavingThrows;

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

  public boolean isSpellCaster() {
    return !spells.isEmpty() || characterClass.isSpellCaster();
  }

  public Set<Enum> getAdvantageOnSavingThrows() {
    Set<Enum> advantages = new HashSet<>();
    advantages.addAll(advantageOnSavingThrows);
    advantages.addAll(race.getAdvantageOnSavingThrows());
    return advantages;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(this.getRace() + " " + this.getCharacterClass() + "\n"
        + "HP:" + this.getMaxHealthPoints() + " AC:" + this.getArmorClass(Weapon.FISTS) + "\n"
        + this.getAbilities() + "\n"
        + "Equipment:\n");
    this
        .getEquipment()
        .getWeapons()
        .stream()
        .map(Weapon::toString)
        .forEach(weapon -> stringBuilder.append("- ").append(weapon).append("\n"));
    this.getEquipment().getArmor().map(Armor::toString).ifPresent(armor -> stringBuilder.append("- ").append(armor).append("\n"));
    this.getEquipment().getShield().map(Armor::toString).ifPresent(shield -> stringBuilder.append("- ").append(shield).append("\n"));
    if (this.getSpells() != null && !this.getSpells().isEmpty()) {
      stringBuilder.append("Additional spells:\n");
      this.getSpells().forEach(spell -> stringBuilder.append("- ").append(spell).append("\n"));
    }
    return stringBuilder.toString();
  }

  public static PropertiesBuilder builder() {
    return new PropertiesBuilder();
  }

}