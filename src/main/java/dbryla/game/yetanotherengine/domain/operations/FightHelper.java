package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.DiceRoll;
import dbryla.game.yetanotherengine.domain.spells.DiceRollModifier;
import dbryla.game.yetanotherengine.domain.subjects.classes.Cleric;
import dbryla.game.yetanotherengine.domain.subjects.classes.Subject;
import dbryla.game.yetanotherengine.domain.subjects.classes.Wizard;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

import static dbryla.game.yetanotherengine.domain.spells.DiceRollModifier.ADVANTAGE;
import static dbryla.game.yetanotherengine.domain.spells.DiceRollModifier.DISADVANTAGE;

@Component
public class FightHelper {

  int getHitRoll(Subject source, Subject target) {
    return applyRulesToHitRoll(source, target);
  }

  private int applyRulesToHitRoll(Subject source, Subject target) {
    Supplier<Integer> hitRoll = DiceRoll::k20;
    int modifiers = 0;
    boolean sourceHasDisadvantage = false;
    if (source.getActiveEffect().isPresent()) {
      DiceRollModifier sourceModifier = source.getActiveEffect().get().getEffect().getSourceHitRollModifier();
      if (isAdvantageOrDisadvantage(sourceModifier)) {
        sourceHasDisadvantage = DISADVANTAGE.equals(sourceModifier);
        hitRoll = sourceModifier::getDiceRollModifier;
      } else {
        modifiers += sourceModifier.getDiceRollModifier();
      }
    }
    if (target.getActiveEffect().isPresent()) {
      DiceRollModifier targetModifier = target.getActiveEffect().get().getEffect().getTargetHitRollModifier();
      if (!sourceHasDisadvantage && isAdvantageOrDisadvantage(targetModifier)) {
        hitRoll = targetModifier::getDiceRollModifier;
      } else {
        modifiers += targetModifier.getDiceRollModifier();
      }
    }
    return hitRoll.get() + modifiers;
  }

  private boolean isAdvantageOrDisadvantage(DiceRollModifier modifier) {
    return DISADVANTAGE.equals(modifier) || ADVANTAGE.equals(modifier);
  }

  public boolean isMiss(int armorClass, int hitRoll, int originalHitRoll) {
    return originalHitRoll == 1 || hitRoll < armorClass;
  }

  public int getAttackDamage(int attackDamage, int hitRoll) {
    if (hitRoll == 20) {
      return attackDamage * 2;
    }
    return attackDamage;
  }

  int getConstitutionSavingThrow(Subject target) {
    return applyRulesToSavingThrow(target) + target.getAbilities().getConstitutionModifier();
  }

  private int applyRulesToSavingThrow(Subject target) {
    Supplier<Integer> hitRoll = DiceRoll::k20;
    int modifiers = 0;
    if (target.getActiveEffect().isPresent()) {
      DiceRollModifier targetModifier = target.getActiveEffect().get().getEffect().getTargetSavingThrowModifier();
      if (isAdvantageOrDisadvantage(targetModifier)) {
        hitRoll = targetModifier::getDiceRollModifier;
      } else {
        modifiers += targetModifier.getDiceRollModifier();
      }
    }
    return hitRoll.get() + modifiers;
  }

  boolean isSaved(Subject source, int savingThrow) {
    return savingThrow >= 8 + getModifier(source);
  }

  int getDexteritySavingThrow(Subject target) {
    return applyRulesToSavingThrow(target) + target.getAbilities().getDexterityModifier();
  }

  int getModifier(Subject source) {
    if (source instanceof Wizard) {
      return source.getAbilities().getIntelligenceModifier();
    }
    if (source instanceof Cleric) {
      return source.getAbilities().getWisdomModifier();
    }
    return 0;
  }
}
