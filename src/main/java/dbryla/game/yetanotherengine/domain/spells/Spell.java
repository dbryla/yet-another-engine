package dbryla.game.yetanotherengine.domain.spells;

import dbryla.game.yetanotherengine.domain.DiceRoll;

import java.util.function.Supplier;

import dbryla.game.yetanotherengine.domain.subjects.classes.Cleric;
import dbryla.game.yetanotherengine.domain.subjects.classes.Wizard;
import lombok.Getter;

import static dbryla.game.yetanotherengine.domain.spells.Effect.*;
import static dbryla.game.yetanotherengine.domain.spells.SpellConstants.UNLIMITED_TARGETS;
import static dbryla.game.yetanotherengine.domain.spells.SpellSaveType.*;
import static dbryla.game.yetanotherengine.domain.spells.SpellType.*;

public enum Spell {

  SACRED_FLAME(Cleric.class,
      DAMAGE, DEXTERITY_SAVING_THROW, DiceRoll::k8, 1),
  BLESS(Cleric.class,
      EFFECT, Effect.BLESS, 3, true),
  HEALING_WORD(Cleric.class,
      HEAL, IRRESISTIBLE, DiceRoll::k4, 1),
  ACID_SPLASH(Wizard.class,
      DAMAGE, DEXTERITY_SAVING_THROW, DiceRoll::k6, 2),
  FIRE_BOLT(Wizard.class,
      DAMAGE, ARMOR_CLASS, DiceRoll::k10, 1),
  POISON_SPRAY(Wizard.class,
      DAMAGE, CONSTITUTION_SAVING_THROW, DiceRoll::k12, 1),
  BURNING_HAND(Wizard.class,
      DAMAGE, DEXTERITY_HALF_SAVING_THROW, () -> DiceRoll.k6() + DiceRoll.k6() + DiceRoll.k6(), UNLIMITED_TARGETS),
  COLOR_SPRAY(Wizard.class,
      EFFECT, BLIND, UNLIMITED_TARGETS, false);

  private final Class owner;
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
  private final boolean friendlyTargets;

  Spell(Class owner,
        SpellType spellType,
        SpellSaveType spellSaveType,
        Supplier<Integer> spellRoll,
        int maximumNumberOfTargets) {
    this.owner = owner;
    this.spellType = spellType;
    this.spellSaveType = spellSaveType;
    this.maximumNumberOfTargets = maximumNumberOfTargets;
    this.spellRoll = spellRoll;
    this.friendlyTargets = !DAMAGE.equals(spellType);
    this.spellEffect = null;
  }

  Spell(Class owner, SpellType spellType, Effect spellEffect, int maximumNumberOfTargets, boolean friendlyTargets) {
    this.owner = owner;
    this.spellType = spellType;
    this.spellEffect = spellEffect;
    this.maximumNumberOfTargets = maximumNumberOfTargets;
    this.friendlyTargets = friendlyTargets;
    this.spellSaveType = IRRESISTIBLE;
    this.spellRoll = null;
  }

  public int spellRoll() {
    return spellRoll.get();
  }

  public boolean forClass(Class clazz) {
    return owner.equals(clazz);
  }
}
