package dbryla.game.yetanotherengine.domain.spells;

import dbryla.game.yetanotherengine.domain.IncorrectStateException;
import dbryla.game.yetanotherengine.domain.Range;
import dbryla.game.yetanotherengine.domain.dice.DiceRollService;
import dbryla.game.yetanotherengine.domain.effects.Effect;
import dbryla.game.yetanotherengine.domain.operations.DamageType;
import dbryla.game.yetanotherengine.domain.subject.CharacterClass;
import dbryla.game.yetanotherengine.domain.subject.Condition;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static dbryla.game.yetanotherengine.domain.battleground.Distance.*;
import static dbryla.game.yetanotherengine.domain.effects.FightEffectLogic.CONCENTRATION;
import static dbryla.game.yetanotherengine.domain.spells.SpellConstants.ALL_TARGETS_WITHIN_RANGE;
import static dbryla.game.yetanotherengine.domain.spells.SpellSaveType.*;
import static dbryla.game.yetanotherengine.domain.spells.SpellType.*;
import static dbryla.game.yetanotherengine.domain.subject.CharacterClass.CLERIC;
import static dbryla.game.yetanotherengine.domain.subject.CharacterClass.WIZARD;

public enum Spell implements Range {

  SACRED_FLAME(CLERIC, 0, DEXTERITY_SAVING_THROW, 1, 8,
      1, SIXTY_FEET, DamageType.RADIANT),
  ACID_SPLASH(WIZARD, 0, DEXTERITY_SAVING_THROW, 1, 6,
      2, SIXTY_FEET, DamageType.ACID),
  POISON_SPRAY(WIZARD, 0, CONSTITUTION_SAVING_THROW, 1, 12,
      1, CLOSE_RANGE, DamageType.POISON),
  BURNING_HANDS(WIZARD, 1, DEXTERITY_HALF_SAVING_THROW, 3, 6,
      ALL_TARGETS_WITHIN_RANGE, CLOSE_RANGE, DamageType.FIRE),

  HEALING_WORD(CLERIC, 1, 1, 4, 1, SIXTY_FEET),
  CURE_WOUNDS(CLERIC, 1, 1, 8, 1, CLOSE_RANGE),

  FIRE_BOLT(WIZARD, 0, 1, 10,
      "%s burns %s to dust with fire bolt.", THIRTY_FEET, ONE_HUNDRED_TWENTY_FEET, DamageType.FIRE),

  BLESS(CLERIC, 0, Effect.BLESSED, 3, true, THIRTY_FEET, CONCENTRATION),
  COLOR_SPRAY(WIZARD, 1, Effect.BLINDED, ALL_TARGETS_WITHIN_RANGE, false, CLOSE_RANGE, 1);

  private final CharacterClass owner;
  private final int spellLevel;
  @Getter
  private final SpellType spellType;
  @Getter
  private final SpellSaveType spellSaveType;
  @Getter
  private final Effect spellEffect;
  private final int spellEffectDurationInTurns;
  private final int numberOfHitDice;
  private final int hitDice;
  @Getter
  private final int maximumNumberOfTargets;
  @Getter
  private final boolean positiveSpell;
  @Getter
  private final String criticalHitMessage;
  @Getter
  private final boolean isModifierApply;
  @Getter
  private final int maxRange;
  @Getter
  private final int minRange;
  @Getter
  private final DamageType damageType;

  /**
   * for damage spells
   */
  Spell(CharacterClass owner, int spellLevel, SpellSaveType spellSaveType,
        int numberOfHitDice, int hitDice, int maximumNumberOfTargets, int maxRange, DamageType damageType) {
    this.owner = owner;
    this.spellLevel = spellLevel;
    this.spellType = DAMAGE;
    this.spellSaveType = spellSaveType;
    this.isModifierApply = false;
    this.numberOfHitDice = numberOfHitDice;
    this.hitDice = hitDice;
    this.maximumNumberOfTargets = maximumNumberOfTargets;
    this.damageType = damageType;
    this.minRange = CLOSE_RANGE;
    this.maxRange = maxRange;
    this.spellEffect = null;
    this.positiveSpell = false;
    this.criticalHitMessage = null;
    this.spellEffectDurationInTurns = 0;
  }

  /**
   * for healing spells
   */
  Spell(CharacterClass owner, int spellLevel, int numberOfHitDice, int hitDice, int maximumNumberOfTargets, int maxRange) {
    this.owner = owner;
    this.spellLevel = spellLevel;
    this.numberOfHitDice = numberOfHitDice;
    this.hitDice = hitDice;
    this.maximumNumberOfTargets = maximumNumberOfTargets;
    this.maxRange = maxRange;
    this.isModifierApply = true;
    this.minRange = CLOSE_RANGE;
    this.spellSaveType = IRRESISTIBLE;
    this.positiveSpell = true;
    this.damageType = null;
    this.spellType = HEAL;
    this.spellEffect = null;
    this.criticalHitMessage = null;
    this.spellEffectDurationInTurns = 0;
  }

  /**
   * for spell attacks
   */
  Spell(CharacterClass owner, int spellLevel, int numberOfHitDice, int hitDice,
        String criticalHitMessage, int minRange, int maxRange, DamageType damageType) {
    this.owner = owner;
    this.spellLevel = spellLevel;
    this.minRange = minRange;
    this.damageType = damageType;
    this.spellType = DAMAGE;
    this.spellSaveType = ARMOR_CLASS;
    this.numberOfHitDice = numberOfHitDice;
    this.hitDice = hitDice;
    this.maximumNumberOfTargets = 1;
    this.positiveSpell = false;
    this.criticalHitMessage = criticalHitMessage;
    this.isModifierApply = false;
    this.maxRange = maxRange;
    this.spellEffect = null;
    this.spellEffectDurationInTurns = 0;
  }

  /**
   * for effect spells
   */
  Spell(CharacterClass owner, int spellLevel, Effect spellEffect,
        int maximumNumberOfTargets, boolean positiveSpell, int maxRange, int durationInTurns) {
    this.owner = owner;
    this.spellLevel = spellLevel;
    this.spellType = EFFECT;
    this.spellEffect = spellEffect;
    this.maximumNumberOfTargets = maximumNumberOfTargets;
    this.positiveSpell = positiveSpell;
    this.maxRange = maxRange;
    this.minRange = CLOSE_RANGE;
    this.spellSaveType = IRRESISTIBLE;
    this.numberOfHitDice = 0;
    this.hitDice = 0;
    this.criticalHitMessage = null;
    this.isModifierApply = false;
    this.damageType = null;
    this.spellEffectDurationInTurns = durationInTurns;
  }

  public static Set<Spell> of(CharacterClass owner, int spellLevel) {
    return Arrays.stream(Spell.values())
        .filter(spell -> owner.equals(spell.owner) && spellLevel == spell.spellLevel)
        .collect(Collectors.toSet());
  }

  public int roll(DiceRollService diceRollService) {
    return IntStream.range(0, numberOfHitDice).map(i -> diceRollService.of(hitDice)).sum();
  }

  public boolean forClass(CharacterClass characterClass) {
    return owner.equals(characterClass);
  }

  @Override
  public String toString() {
    return super.toString().toLowerCase().replace("_", " ");
  }

  public boolean isAreaOfEffectSpell() {
    return maximumNumberOfTargets == ALL_TARGETS_WITHIN_RANGE;
  }

  public Condition cast() {
    return spellEffect.activate(this.spellEffectDurationInTurns);
  }

  @Override
  public boolean isClose() {
    return minRange == CLOSE_RANGE;
  }

  public Enum getDamageTypeOrEffect() {
    switch (spellType) {
      case EFFECT:
        return spellEffect;
      case DAMAGE:
        return damageType;
    }
    throw new IncorrectStateException("Spell of type: " + spellType + " can't be mitigated.");
  }
}
