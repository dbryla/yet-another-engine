package dbryla.game.yetanotherengine.domain.spells;

import static dbryla.game.yetanotherengine.domain.battleground.Distance.CLOSE_RANGE;
import static dbryla.game.yetanotherengine.domain.battleground.Distance.ONE_HUNDRED_TWENTY_FEET;
import static dbryla.game.yetanotherengine.domain.battleground.Distance.SIXTY_FEET;
import static dbryla.game.yetanotherengine.domain.battleground.Distance.THIRTY_FEET;
import static dbryla.game.yetanotherengine.domain.spells.SpellConstants.UNLIMITED_TARGETS;
import static dbryla.game.yetanotherengine.domain.spells.SpellSaveType.ARMOR_CLASS;
import static dbryla.game.yetanotherengine.domain.spells.SpellSaveType.CONSTITUTION_SAVING_THROW;
import static dbryla.game.yetanotherengine.domain.spells.SpellSaveType.DEXTERITY_HALF_SAVING_THROW;
import static dbryla.game.yetanotherengine.domain.spells.SpellSaveType.DEXTERITY_SAVING_THROW;
import static dbryla.game.yetanotherengine.domain.spells.SpellSaveType.IRRESISTIBLE;
import static dbryla.game.yetanotherengine.domain.spells.SpellType.DAMAGE;
import static dbryla.game.yetanotherengine.domain.spells.SpellType.EFFECT;
import static dbryla.game.yetanotherengine.domain.spells.SpellType.HEAL;
import static dbryla.game.yetanotherengine.domain.subject.CharacterClass.CLERIC;
import static dbryla.game.yetanotherengine.domain.subject.CharacterClass.WIZARD;

import dbryla.game.yetanotherengine.domain.dice.DiceRollService;
import dbryla.game.yetanotherengine.domain.effects.Effect;
import dbryla.game.yetanotherengine.domain.subject.CharacterClass;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;
import lombok.Getter;

public enum Spell {

  SACRED_FLAME(CLERIC, 0, DAMAGE, DEXTERITY_SAVING_THROW, false, 1, 8, 1, SIXTY_FEET),
  BLESS(CLERIC, 0, EFFECT, Effect.BLESS, 3, true, THIRTY_FEET),
  HEALING_WORD(CLERIC, 1, HEAL, IRRESISTIBLE, true, 1, 4, 1, SIXTY_FEET),
  ACID_SPLASH(WIZARD, 0, DAMAGE, DEXTERITY_SAVING_THROW, false, 1, 6, 2, SIXTY_FEET),
  FIRE_BOLT(WIZARD, 0, false, 1, 10, 1, "$s burns $s to dust with fire bolt.", THIRTY_FEET, ONE_HUNDRED_TWENTY_FEET),
  POISON_SPRAY(WIZARD, 0, DAMAGE, CONSTITUTION_SAVING_THROW, false, 1, 12, 1, CLOSE_RANGE),
  BURNING_HANDS(WIZARD, 1, DAMAGE, DEXTERITY_HALF_SAVING_THROW, false, 3, 6, UNLIMITED_TARGETS, CLOSE_RANGE),
  COLOR_SPRAY(WIZARD, 1, EFFECT, Effect.BLIND, UNLIMITED_TARGETS, false, CLOSE_RANGE);

  private final CharacterClass owner;
  private final int spellLevel;
  @Getter
  private final SpellType spellType;
  @Getter
  private final SpellSaveType spellSaveType;
  @Getter
  private final Effect spellEffect;
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

  /**
   * for spell attacks
   */
  Spell(CharacterClass owner, int spellLevel, boolean isModifierApply, int numberOfHitDice, int hitDice,
      int maximumNumberOfTargets, String criticalHitMessage, int minRange, int maxRange) {
    this.owner = owner;
    this.spellLevel = spellLevel;
    this.minRange = minRange;
    this.spellType = DAMAGE;
    this.spellSaveType = ARMOR_CLASS;
    this.numberOfHitDice = numberOfHitDice;
    this.hitDice = hitDice;
    this.maximumNumberOfTargets = maximumNumberOfTargets;
    this.positiveSpell = false;
    this.criticalHitMessage = criticalHitMessage;
    this.isModifierApply = isModifierApply;
    this.maxRange = maxRange;
    this.spellEffect = null;
  }

  Spell(CharacterClass owner, int spellLevel, SpellType spellType, Effect spellEffect,
      int maximumNumberOfTargets, boolean positiveSpell, int maxRange) {
    this.owner = owner;
    this.spellLevel = spellLevel;
    this.spellType = spellType;
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
  }

  Spell(CharacterClass owner, int spellLevel, SpellType spellType, SpellSaveType spellSaveType, boolean isModifierApply,
      int numberOfHitDice, int hitDice, int maximumNumberOfTargets, int maxRange) {
    this.owner = owner;
    this.spellLevel = spellLevel;
    this.spellType = spellType;
    this.spellSaveType = spellSaveType;
    this.isModifierApply = isModifierApply;
    this.numberOfHitDice = numberOfHitDice;
    this.hitDice = hitDice;
    this.maximumNumberOfTargets = maximumNumberOfTargets;
    this.minRange = CLOSE_RANGE;
    this.maxRange = maxRange;
    this.spellEffect = null;
    this.positiveSpell = !DAMAGE.equals(spellType);
    this.criticalHitMessage = null;
  }

  public static Optional<Spell> of(CharacterClass owner, int spellLevel) { // return random spell for now
    return Arrays.stream(Spell.values()).filter(spell -> owner.equals(spell.owner) && spellLevel == spell.spellLevel).findFirst();
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

  public boolean hasUnlimitedTargets() {
    return maximumNumberOfTargets == SpellConstants.UNLIMITED_TARGETS;
  }

}
