package dbryla.game.yetanotherengine.domain.spells;

import static dbryla.game.yetanotherengine.domain.spells.SpellConstants.UNLIMITED_TARGETS;
import static dbryla.game.yetanotherengine.domain.spells.SpellSaveType.ARMOR_CLASS;
import static dbryla.game.yetanotherengine.domain.spells.SpellSaveType.CONSTITUTION_SAVING_THROW;
import static dbryla.game.yetanotherengine.domain.spells.SpellSaveType.DEXTERITY_HALF_SAVING_THROW;
import static dbryla.game.yetanotherengine.domain.spells.SpellSaveType.DEXTERITY_SAVING_THROW;
import static dbryla.game.yetanotherengine.domain.spells.SpellSaveType.IRRESISTIBLE;
import static dbryla.game.yetanotherengine.domain.spells.SpellType.DAMAGE;
import static dbryla.game.yetanotherengine.domain.spells.SpellType.EFFECT;
import static dbryla.game.yetanotherengine.domain.spells.SpellType.HEAL;

import dbryla.game.yetanotherengine.domain.DiceRoll;
import dbryla.game.yetanotherengine.domain.subjects.classes.Cleric;
import dbryla.game.yetanotherengine.domain.subjects.classes.Wizard;
import java.util.function.Supplier;
import lombok.Getter;

public enum Spell {

  SACRED_FLAME(Cleric.class, 0, DAMAGE, DEXTERITY_SAVING_THROW, false,
      DiceRoll::k8, 1, null),
  BLESS(Cleric.class, 0, EFFECT,
      Effect.BLESS, 3, true),
  HEALING_WORD(Cleric.class, 1, HEAL, IRRESISTIBLE, true,
      DiceRoll::k4, 1, null),
  ACID_SPLASH(Wizard.class, 0, DAMAGE, DEXTERITY_SAVING_THROW, false, DiceRoll::k6, 2, null),
  FIRE_BOLT(Wizard.class, 0, DAMAGE, ARMOR_CLASS, false,
      DiceRoll::k10, 1, "$s burns $s to dust with fire bolt."),
  POISON_SPRAY(Wizard.class, 0, DAMAGE, CONSTITUTION_SAVING_THROW, false,
      DiceRoll::k12, 1, null),
  BURNING_HAND(Wizard.class, 1, DAMAGE, DEXTERITY_HALF_SAVING_THROW, false,
      () -> DiceRoll.k6() + DiceRoll.k6() + DiceRoll.k6(), UNLIMITED_TARGETS, null),
  COLOR_SPRAY(Wizard.class, 1, EFFECT,
      Effect.BLIND, UNLIMITED_TARGETS, false);

  private final Class owner;
  private final int spellLevel;
  @Getter
  private final SpellType spellType;
  @Getter
  private final SpellSaveType spellSaveType;
  @Getter
  private final Effect spellEffect;
  @Getter
  private final int maximumNumberOfTargets;
  private final Supplier<Integer> spellRoll;
  @Getter
  private final boolean positiveSpell;
  @Getter
  private final String criticalHitMessage;
  @Getter
  private final boolean isModifierApply;

  Spell(Class owner, int spellLevel, SpellType spellType, SpellSaveType spellSaveType, boolean isModifierApply,
      Supplier<Integer> spellRoll, int maximumNumberOfTargets, String criticalHitMessage) {
    this.owner = owner;
    this.spellLevel = spellLevel;
    this.spellType = spellType;
    this.spellSaveType = spellSaveType;
    this.maximumNumberOfTargets = maximumNumberOfTargets;
    this.spellRoll = spellRoll;
    this.positiveSpell = !DAMAGE.equals(spellType);
    this.criticalHitMessage = criticalHitMessage;
    this.isModifierApply = isModifierApply;
    this.spellEffect = null;
  }

  Spell(Class owner, int spellLevel, SpellType spellType, Effect spellEffect, int maximumNumberOfTargets, boolean positiveSpell) {
    this.owner = owner;
    this.spellLevel = spellLevel;
    this.spellType = spellType;
    this.spellEffect = spellEffect;
    this.maximumNumberOfTargets = maximumNumberOfTargets;
    this.positiveSpell = positiveSpell;
    this.spellSaveType = IRRESISTIBLE;
    this.spellRoll = null;
    this.criticalHitMessage = null;
    this.isModifierApply = false;
  }

  public int spellRoll() {
    return spellRoll.get();
  }

  public boolean forClass(Class clazz) {
    return owner.equals(clazz);
  }
}
