package dbryla.game.yetanotherengine.domain.spells;

import static dbryla.game.yetanotherengine.domain.spells.SpellConstants.UNLIMITED_TARGETS;
import static dbryla.game.yetanotherengine.domain.spells.SpellConstants.DAMAGE;
import static dbryla.game.yetanotherengine.domain.spells.SpellConstants.EFFECT;
import static dbryla.game.yetanotherengine.domain.spells.SpellConstants.IRRESISTIBLE;
import static dbryla.game.yetanotherengine.domain.spells.SpellConstants.SPELL_ATTACK;

import dbryla.game.yetanotherengine.domain.DiceRoll;
import java.util.function.Supplier;
import lombok.Getter;

public enum Spell {

  FIRE_BOLT(DAMAGE, SPELL_ATTACK, DiceRoll::k10, 1),
  COLOR_SPRAY(EFFECT, IRRESISTIBLE, Effect.BLIND, UNLIMITED_TARGETS);

  @Getter
  private final String damageType;
  @Getter
  private final String spellSaveType;
  @Getter
  private final Effect spellEffect;
  @Getter
  private final int numberOfTargets;
  private final Supplier<Integer> rollForDamage;

  Spell(String damageType, String spellSaveType, Supplier<Integer> rollForDamage, int numberOfTargets) {
    this.damageType = damageType;
    this.spellSaveType = spellSaveType;
    this.numberOfTargets = numberOfTargets;
    this.rollForDamage = rollForDamage;
    this.spellEffect = null;
  }

  Spell(String damageType, String spellSaveType, Effect spellEffect, int numberOfTargets) {
    this.damageType = damageType;
    this.spellSaveType = spellSaveType;
    this.numberOfTargets = numberOfTargets;
    this.rollForDamage = null;
    this.spellEffect = spellEffect;
  }

  public int attackDamageRoll() {
    return rollForDamage.get();
  }

}