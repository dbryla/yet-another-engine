package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.Range;
import dbryla.game.yetanotherengine.domain.dice.AdvantageRollModifier;
import dbryla.game.yetanotherengine.domain.dice.DiceRollService;
import dbryla.game.yetanotherengine.domain.dice.DisadvantageRollModifier;
import dbryla.game.yetanotherengine.domain.dice.HitDiceRollModifier;
import dbryla.game.yetanotherengine.domain.effects.Effect;
import dbryla.game.yetanotherengine.domain.effects.FightEffectsMapper;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.CharacterClass;
import dbryla.game.yetanotherengine.domain.subject.Race;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static dbryla.game.yetanotherengine.domain.effects.Effect.*;
import static dbryla.game.yetanotherengine.domain.operations.DamageType.DISEASE;
import static dbryla.game.yetanotherengine.domain.operations.DamageType.POISON;
import static dbryla.game.yetanotherengine.domain.operations.HitResult.CRITICAL;

@Component
@AllArgsConstructor
class FightHelper {

  private static final Set<Effect> NO_SAVE_THROW_ON_STRENGTH_AND_DEXTERITY = Set.of(PARALYZED, PETRIFIED, STUNNED, UNCONSCIOUS);
  private final DiceRollService diceRollService;
  private final FightEffectsMapper fightEffectsMapper;
  private final AdvantageRollModifier advantageRollModifier;
  private final DisadvantageRollModifier disadvantageRollModifier;

  HitRoll getHitRoll(Subject source, Range range, Subject target) {
    Set<HitDiceRollModifier> modifiers = source
        .getConditions()
        .stream()
        .map(condition -> fightEffectsMapper.getLogic(condition.getEffect()).getSourceHitRollModifier())
        .collect(Collectors.toSet());
    modifiers.addAll(target
        .getConditions()
        .stream()
        .map(condition -> fightEffectsMapper.getLogic(condition.getEffect()).getTargetHitRollModifier(range))
        .collect(Collectors.toSet()));
    cancelOppositeModifiers(modifiers);
    int hitRoll = diceRollService.k20();
    int hitRollModifier = 0;
    for (HitDiceRollModifier modifier : modifiers) {
      if (modifier.canModifyOriginalHitRoll()) {
        hitRoll = modifier.apply(hitRoll);
      } else {
        hitRollModifier += modifier.apply(hitRoll);
      }
    }
    hitRoll = handleLuckyEffect(source, hitRoll);
    return new HitRoll(hitRoll, hitRollModifier);
  }

  private int handleLuckyEffect(Subject source, int hitRoll) {
    if (source.getRace().getRaceEffects().contains(LUCKY)) {
      return fightEffectsMapper.getLogic(LUCKY).getSourceHitRollModifier().apply(hitRoll);
    }
    return hitRoll;
  }

  int getAttackDamage(Subject source, Supplier<Integer> attackDamageSupplier, HitResult hitResult) {
    int attackDamage = attackDamageSupplier.get();
    if (CRITICAL.equals(hitResult)) {
      attackDamage += attackDamageSupplier.get();
      if (source.getRace().getRaceEffects().contains(SAVAGE_ATTACK)) {
        attackDamage += attackDamageSupplier.get();
      }
    }
    return attackDamage;
  }

  int getConstitutionSavingThrow(Subject target, Spell spell) {
    return applyRulesToSavingThrow(target, spell) + target.getAbilities().getConstitutionModifier();
  }

  private int applyRulesToSavingThrow(Subject target, Spell spell) {
    return applyRulesToSavingThrow(target, spell, false);
  }

  private int applyRulesToSavingThrow(Subject target, Spell spell, boolean hasDisadvantage) {
    Set<HitDiceRollModifier> modifiers = target
        .getConditions()
        .stream()
        .map(condition -> fightEffectsMapper.getLogic(condition.getEffect()).getTargetSavingThrowModifier())
        .collect(Collectors.toSet());
    if (hasDisadvantage) {
      modifiers.add(disadvantageRollModifier);
    }
    if (spell != null && target.getAdvantageOnSavingThrows().contains(spell.getDamageTypeOrEffect())) {
      modifiers.add(advantageRollModifier);
    }
    cancelOppositeModifiers(modifiers);
    int hitRoll = diceRollService.k20();
    int hitRollModifier = 0;
    for (HitDiceRollModifier modifier : modifiers) {
      if (modifier.canModifyOriginalHitRoll()) {
        hitRoll = modifier.apply(hitRoll);
      } else {
        hitRollModifier += modifier.apply(hitRoll);
      }
    }
    hitRoll = handleLuckyEffect(target, hitRoll);
    return hitRoll + hitRollModifier;
  }

  private void cancelOppositeModifiers(Set<HitDiceRollModifier> modifiers) {
    if (modifiers.contains(advantageRollModifier) && modifiers.contains(disadvantageRollModifier)) {
      modifiers.removeAll(Set.of(advantageRollModifier, disadvantageRollModifier));
    }
  }

  int getDexteritySavingThrow(Subject target, Spell spell) {
    if (noSaveThrowOnStrengthOrDexterity(target)) {
      return 0;
    }
    return applyRulesToSavingThrow(target, spell, isRestrained(target)) + target.getAbilities().getDexterityModifier();
  }

  private boolean noSaveThrowOnStrengthOrDexterity(Subject target) {
    return target.getConditions().stream().anyMatch(condition -> NO_SAVE_THROW_ON_STRENGTH_AND_DEXTERITY.contains(condition.getEffect()));
  }

  private boolean isRestrained(Subject target) {
    return target.getConditions().stream().anyMatch(condition -> RESTRAINED.equals(condition.getEffect()));
  }

  int getStrengthSavingThrow(Subject target) {
    if (noSaveThrowOnStrengthOrDexterity(target)) {
      return 0;
    }
    return applyRulesToSavingThrow(target, null) + target.getAbilities().getStrengthModifier();
  }

  boolean isSaved(Subject source, Spell spell, int savingThrow) {
    return savingThrow >= 8 + getModifier(source, spell);
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
    Race targetRace = target.getRace();
    if (targetRace.getImmunities().contains(damageType) || (isPetrified(target) && Set.of(POISON, DISEASE).contains(damageType))) {
      return Optional.empty();
    }
    if (targetRace.getResistances().contains(damageType) || isPetrified(target)) {
      attackDamage = attackDamage / 2;
    }
    if (targetRace.getVulnerabilities().contains(damageType)) {
      attackDamage = attackDamage * 2;
    }
    int remainingHealthPoints = target.getCurrentHealthPoints() - attackDamage;
    if (remainingHealthPoints == 0 && targetRace.getRaceEffects().contains(RELENTLESS_ENDURANCE)) {
      return Optional.of(target.of(1));
    }
    return Optional.of(target.of(remainingHealthPoints));
  }

  private boolean isPetrified(Subject target) {
    return target.getConditions().stream().anyMatch(condition -> PETRIFIED.equals(condition.getEffect()));
  }
}
