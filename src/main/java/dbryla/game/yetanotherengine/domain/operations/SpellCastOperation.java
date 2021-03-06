package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.dice.DiceRollService;
import dbryla.game.yetanotherengine.domain.events.Event;
import dbryla.game.yetanotherengine.domain.events.EventFactory;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.spells.SpellSaveType;
import dbryla.game.yetanotherengine.domain.subject.State;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

import static dbryla.game.yetanotherengine.domain.spells.SpellConstants.ALL_TARGETS_WITHIN_RANGE;
import static dbryla.game.yetanotherengine.domain.spells.SpellType.*;

@AllArgsConstructor
@Component
public class SpellCastOperation implements Operation {

  private final FightHelper fightHelper;
  private final EventFactory eventFactory;
  private final DiceRollService diceRollService;

  @Override
  public OperationResult invoke(Subject source, ActionData actionData, Subject... targets) throws UnsupportedGameOperationException {
    Spell spell = actionData.getSpell();
    verifyTargetsNumber(targets, spell);
    OperationResult operationResult = new OperationResult();
    if (DAMAGE.equals(spell.getSpellType())) {
      OperationResult op = tryToDealDamage(source, spell, targets);
      operationResult.addAll(op.getChangedSubjects(), op.getEmittedEvents());
    }
    if (EFFECT.equals(spell.getSpellType())) {
      for (Subject target : targets) {
        OperationResult op = applyEffect(source, spell, target);
        operationResult.addAll(op.getChangedSubjects(), op.getEmittedEvents());
      }
    }
    if (HEAL.equals(spell.getSpellType())) {
      int healRoll = spell.roll(diceRollService);
      healRoll += getModifier(source, spell);
      if (healRoll <= 0) {
        healRoll = 1;
      }
      for (Subject target : targets) {
        OperationResult op = heal(source, target, healRoll);
        operationResult.addAll(op.getChangedSubjects(), op.getEmittedEvents());
      }
    }
    return operationResult;
  }

  private void verifyTargetsNumber(Subject[] targets, Spell spell) throws UnsupportedSpellCastException {
    if (!unlimitedTargets(spell) && spell.getMaximumNumberOfTargets() < targets.length) {
      throw new UnsupportedSpellCastException("Can't invoke spell " + spell + " on " + targets.length + " targets.");
    }
  }

  private boolean unlimitedTargets(Spell spell) {
    return spell.getMaximumNumberOfTargets() == ALL_TARGETS_WITHIN_RANGE;
  }

  private OperationResult tryToDealDamage(Subject source, Spell spell, Subject[] targets) {
    if (SpellSaveType.ARMOR_CLASS.equals(spell.getSpellSaveType())) {
      return handleSpellAttack(source, spell, targets);
    }
    int attackDamage = spell.roll(diceRollService);
    attackDamage += getModifier(source, spell);
    if (attackDamage <= 0) {
      attackDamage = 1;
    }
    if (SpellSaveType.CONSTITUTION_SAVING_THROW.equals(spell.getSpellSaveType())) {
      return handleSavingThrow(source, spell, targets,
          attackDamage, target -> fightHelper.getConstitutionSavingThrow(target, spell),
          target -> new OperationResult()
              .add(eventFactory.failEventBySavingThrow(source, spell, target)));
    }
    if (SpellSaveType.DEXTERITY_SAVING_THROW.equals(spell.getSpellSaveType())) {
      return handleSavingThrow(source, spell, targets,
          attackDamage, target -> fightHelper.getDexteritySavingThrow(target, spell),
          target -> new OperationResult()
              .add(eventFactory.failEventBySavingThrow(source, spell, target)));
    }
    int attackDamageOnSavedThrow = attackDamage / 2;
    if (SpellSaveType.DEXTERITY_HALF_SAVING_THROW.equals(spell.getSpellSaveType())) {
      return handleSavingThrow(source, spell, targets, attackDamage,
          target -> fightHelper.getDexteritySavingThrow(target, spell),
          target -> dealDamage(source, target, attackDamageOnSavedThrow / 2, spell));
    }
    return new OperationResult();
  }

  private int getModifier(Subject source, Spell spell) {
    if (spell.isModifierApply()) {
      return fightHelper.getModifier(source, spell);
    }
    return 0;
  }

  private OperationResult handleSpellAttack(Subject source, Spell spell, Subject[] targets) {
    Subject target = targets[0]; // spell attacks can attack only single target
    HitRoll hitRoll = fightHelper.getHitRoll(source, spell, target);
    hitRoll.addModifier(fightHelper.getModifier(source, spell));
    HitResult hitResult = HitResult.of(hitRoll, target);
    if (!hitResult.isTargetHit()) {
      return new OperationResult().add(
          eventFactory.failEvent(source, target, spell.toString(), hitResult));
    } else {
      int attackDamage = fightHelper.getAttackDamage(source, () -> spell.roll(diceRollService), hitResult);
      attackDamage += getModifier(source, spell);
      return dealDamage(source, target, attackDamage, spell, hitResult);
    }
  }

  private OperationResult dealDamage(Subject source, Subject target, int attackDamage, Spell spell, HitResult hitResult) {
    return fightHelper
        .dealDamage(target, attackDamage, spell.getDamageType())
        .map(changedTarget -> {
          Event event = eventFactory.successSpellCastEvent(source, changedTarget, spell, hitResult);
          return new OperationResult(changedTarget, event);
        })
        .orElseGet(() ->
            new OperationResult().add(eventFactory.targetImmuneEvent(source, target, spell)));
  }

  private OperationResult handleSavingThrow(Subject source, Spell spell, Subject[] targets, int attackDamage,
      Function<Subject, Integer> savingThrowSupplier, Function<Subject, OperationResult> failAction) {
    OperationResult operationResult = new OperationResult();
    for (Subject target : targets) {
      int savingThrow = savingThrowSupplier.apply(target);
      if (fightHelper.isSaved(source, spell, savingThrow)) {
        OperationResult op = failAction.apply(target);
        operationResult.addAll(op.getChangedSubjects(), op.getEmittedEvents());
      } else {
        OperationResult op = dealDamage(source, target, attackDamage, spell);
        operationResult.addAll(op.getChangedSubjects(), op.getEmittedEvents());
      }
    }
    return operationResult;
  }

  private OperationResult dealDamage(Subject source, Subject target, int attackDamage, Spell spell) {
    return fightHelper.dealDamage(target, attackDamage, spell.getDamageType())
        .map(changedTarget -> {
          Event event = eventFactory.successSpellCastEvent(source, changedTarget, spell);
          return new OperationResult(changedTarget, event);
        })
        .orElseGet(() ->
            new OperationResult().add(eventFactory.targetImmuneEvent(source, target, spell)));
  }

  private OperationResult applyEffect(Subject source, Spell spell, Subject target) {
    State changedTarget = target.withCondition(spell.cast()).getState();
    Event event = eventFactory.successSpellCastEvent(source, changedTarget, spell);
    return new OperationResult(changedTarget, event);
  }

  private OperationResult heal(Subject source, Subject target, int healRoll) {
    State changedTarget = target.withHealthPoints(Math.min(target.getCurrentHealthPoints() + healRoll, target.getMaxHealthPoints()));
    Event event = eventFactory.successHealEvent(source, changedTarget);
    return new OperationResult(changedTarget, event);
  }

}
