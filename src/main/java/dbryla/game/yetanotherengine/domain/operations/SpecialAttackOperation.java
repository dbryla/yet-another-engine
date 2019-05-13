package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.effects.Effect;
import dbryla.game.yetanotherengine.domain.encounters.SpecialAttack;
import dbryla.game.yetanotherengine.domain.events.EventFactory;
import dbryla.game.yetanotherengine.domain.subject.State;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import static dbryla.game.yetanotherengine.domain.effects.Effect.PRONE;
import static dbryla.game.yetanotherengine.domain.effects.FightEffectLogic.FOREVER;

@AllArgsConstructor
@Component
public class SpecialAttackOperation implements Operation {

  private final AttackOperation attackOperation;
  private final MoveOperation moveOperation;
  private final FightHelper fightHelper;
  private final EventFactory eventFactory;

  @Override
  public OperationResult invoke(Subject source, ActionData actionData, Subject... targets) throws UnsupportedGameOperationException {
    SpecialAttack specialAttack = actionData.getSpecialAttack();
    switch (specialAttack) {
      case MULTI_ATTACK:
        return handleMultiAttack(source, targets[0], actionData);
      case POUNCE:
        return handlePounce(source, targets[0], actionData);
    }
    return new OperationResult();
  }

  private OperationResult handleMultiAttack(Subject source, Subject target, ActionData actionData)
      throws UnsupportedGameOperationException {
    ActionData attackData = actionData.getSpecialAttack().getNestedActionData().get(0);
    OperationResult operationResult = attackOperation.invoke(source, attackData, target);
    if (!operationResult.getChangedSubjects().isEmpty() && operationResult.getChangedSubjects().get(0).getCurrentHealthPoints() <= 0) {
      return operationResult;
    }
    operationResult.copyFrom(
        attackOperation.invoke(source.of(source.withCondition(Effect.MULTI_ATTACK.activate(1))), attackData, target));
    return operationResult;
  }

  private OperationResult handlePounce(Subject source, Subject target, ActionData actionData) throws UnsupportedGameOperationException {
    OperationResult operationResult = moveOperation.invoke(source, new ActionData(target.getPosition()));
    State movedSource = operationResult.getChangedSubjects().get(0);
    operationResult.copyFrom(
        attackOperation.invoke(source.of(movedSource), actionData.getSpecialAttack().getNestedActionData().get(0), target));
    if (!operationResult.getChangedSubjects().isEmpty()) {
      State changedTargetState = operationResult.getChangedSubjects().get(0);
      if (changedTargetState.getCurrentHealthPoints() <= 0) {
        return operationResult;
      }
      int savingThrow = fightHelper.getStrengthSavingThrow(target.of(changedTargetState));
      if (savingThrow < 12) {
        changedTargetState = changedTargetState.of(PRONE.activate(FOREVER));
        operationResult.add(changedTargetState, eventFactory.successKnockedProneEvent(source, target));
        operationResult.copyFrom(
            attackOperation.invoke(source, actionData.getSpecialAttack().getNestedActionData().get(1), target.of(changedTargetState)));
      }
    }
    return operationResult;
  }
}
