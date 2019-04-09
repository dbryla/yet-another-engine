package dbryla.game.yetanotherengine.domain.operations;

import static dbryla.game.yetanotherengine.domain.operations.HitResult.CRITICAL;
import static dbryla.game.yetanotherengine.domain.spells.DiceRollModifier.ADVANTAGE;
import static dbryla.game.yetanotherengine.domain.spells.DiceRollModifier.DISADVANTAGE;

import dbryla.game.yetanotherengine.domain.DiceRoll;
import dbryla.game.yetanotherengine.domain.spells.DiceRollModifier;
import dbryla.game.yetanotherengine.domain.subjects.Monster;
import dbryla.game.yetanotherengine.domain.subjects.classes.Cleric;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.domain.subjects.classes.Wizard;

import java.util.function.Supplier;

import org.springframework.stereotype.Component;

@Component
public class FightHelper {

  HitRoll getHitRoll(Subject source, Subject target) {
    return applyRulesToHitRoll(source, target);
  }

  private HitRoll applyRulesToHitRoll(Subject source, Subject target) {
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
    return new HitRoll(hitRoll.get(), modifiers);
  }

  private boolean isAdvantageOrDisadvantage(DiceRollModifier modifier) {
    return DISADVANTAGE.equals(modifier) || ADVANTAGE.equals(modifier);
  }

  int getAttackDamage(int attackDamage, HitResult hitResult) {
    if (CRITICAL.equals(hitResult)) {
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
    if (source instanceof Cleric || source instanceof Monster) {
      return source.getAbilities().getWisdomModifier();
    }
    return 0;
  }
}
