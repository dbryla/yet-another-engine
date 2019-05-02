package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.dice.AdvantageRollModifier;
import dbryla.game.yetanotherengine.domain.dice.DiceRollService;
import dbryla.game.yetanotherengine.domain.dice.DisadvantageRollModifier;
import dbryla.game.yetanotherengine.domain.dice.HitDiceRollModifier;
import dbryla.game.yetanotherengine.domain.effects.EffectsMapper;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.ActiveEffect;
import dbryla.game.yetanotherengine.domain.subject.CharacterClass;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static dbryla.game.yetanotherengine.domain.effects.Effect.LUCKY;
import static dbryla.game.yetanotherengine.domain.effects.Effect.RELENTLESS_ENDURANCE;
import static dbryla.game.yetanotherengine.domain.operations.HitResult.CRITICAL;

@Component
@AllArgsConstructor
class FightHelper {

  private final DiceRollService diceRollService;
  private final EffectsMapper effectsMapper;

  HitRoll getHitRoll(Subject source, Subject target) {
    int hitRoll = diceRollService.k20();
    int modifiers = 0;
    boolean sourceHasDisadvantage = false;
    boolean sourceHasAdvantage = false;
    for (ActiveEffect activeEffect : source.getActiveEffects()) {
      HitDiceRollModifier sourceModifier = effectsMapper.getLogic(activeEffect.getEffect()).getSourceHitRollModifier();
      if (sourceModifier.canModifyOriginalHitRoll()) {
        sourceHasDisadvantage = sourceModifier instanceof DisadvantageRollModifier;
        sourceHasAdvantage = sourceModifier instanceof AdvantageRollModifier;
        hitRoll = sourceModifier.apply(hitRoll);
      } else {
        modifiers += sourceModifier.apply(hitRoll);
      }
    }
    hitRoll = handleLuckyEffect(source, hitRoll);
    for (ActiveEffect activeEffect : target.getActiveEffects()) {
      HitDiceRollModifier targetModifier = effectsMapper.getLogic(activeEffect.getEffect()).getTargetHitRollModifier();
      if (targetModifier.canModifyOriginalHitRoll()) {
        if ((sourceHasAdvantage && targetModifier instanceof DisadvantageRollModifier)
            || (sourceHasDisadvantage && targetModifier instanceof AdvantageRollModifier)) {
          continue;
        }
        hitRoll = targetModifier.apply(hitRoll);
      } else {
        modifiers += targetModifier.apply(hitRoll);
      }
    }
    return new HitRoll(hitRoll, modifiers);
  }

  private Integer handleLuckyEffect(Subject source, Integer hitRoll) {
    if (source.getRace().getRaceEffects().contains(LUCKY)) {
      HitDiceRollModifier sourceModifier = effectsMapper.getLogic(LUCKY).getSourceHitRollModifier();
      hitRoll = sourceModifier.apply(hitRoll);
    }
    return hitRoll;
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
    int hitRoll = diceRollService.k20();
    int modifiers = 0;
    for (ActiveEffect activeEffect : target.getActiveEffects()) {
      HitDiceRollModifier targetModifier = effectsMapper.getLogic(activeEffect.getEffect()).getTargetSavingThrowModifier();
      if (targetModifier.canModifyOriginalHitRoll()) {
        hitRoll = targetModifier.apply(hitRoll);
      } else {
        modifiers += targetModifier.apply(hitRoll);
      }
    }
    hitRoll = handleLuckyEffect(target, hitRoll);
    return hitRoll + modifiers;
  }

  boolean isSaved(Subject source, Spell spell, int savingThrow) {
    return savingThrow >= 8 + getModifier(source, spell);
  }

  int getDexteritySavingThrow(Subject target) {
    return applyRulesToSavingThrow(target) + target.getAbilities().getDexterityModifier();
  }

  int getStrengthSavingThrow(Subject target) {
    return applyRulesToSavingThrow(target) + target.getAbilities().getStrengthModifier();
  }

  int getModifier(Subject source, Spell spell) {
    if (spell.forClass(CharacterClass.WIZARD)) {
      return source.getAbilities().getIntelligenceModifier();
    }
    if (spell.forClass(CharacterClass.CLERIC)) {
      return source.getAbilities().getWisdomModifier();
    }
    return 0;
  }

  Optional<Subject> dealDamage(Subject target, int attackDamage, DamageType damageType) {
    if (target.getImmunities().contains(damageType)) {
      return Optional.empty();
    }
    int remainingHealthPoints = target.getCurrentHealthPoints() - attackDamage;
    if (remainingHealthPoints == 0 && target.getRace().getRaceEffects().contains(RELENTLESS_ENDURANCE)) {
      return Optional.of(target.of(1));
    }
    return Optional.of(target.of(remainingHealthPoints));
  }
}
