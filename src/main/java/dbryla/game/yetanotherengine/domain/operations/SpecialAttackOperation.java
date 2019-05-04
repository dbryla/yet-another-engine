package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.effects.Effect;
import dbryla.game.yetanotherengine.domain.encounters.SpecialAttack;
import dbryla.game.yetanotherengine.domain.events.EventFactory;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class SpecialAttackOperation {

  private final AttackOperation attackOperation;
  private final MoveOperation moveOperation;
  private final FightHelper fightHelper;
  private final EventFactory eventFactory;

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
    int indexOfTarget = operationResult.getChangedSubjects().indexOf(target);
    if (indexOfTarget != -1 && operationResult.getChangedSubjects().get(indexOfTarget).getCurrentHealthPoints() <= 0) {
      return operationResult;
    }
    operationResult.copyFrom(attackOperation.invoke(source.of(Effect.MULTI_ATTACK.activate(1)), attackData, target));
    return operationResult;
  }

  private OperationResult handlePounce(Subject source, Subject target, ActionData actionData) throws UnsupportedGameOperationException {
    OperationResult operationResult = moveOperation.invoke(source, new ActionData(target.getPosition()));
    Subject movedSource = operationResult.getChangedSubjects().get(0);
    operationResult.copyFrom(attackOperation.invoke(movedSource, actionData.getSpecialAttack().getNestedActionData().get(0), target));
    int indexOfTarget = operationResult.getChangedSubjects().indexOf(target);
    if (indexOfTarget != -1) {
      Subject changedTarget = operationResult.getChangedSubjects().get(indexOfTarget);
      if (changedTarget.getCurrentHealthPoints() <= 0) {
        return operationResult;
      }
      int savingThrow = fightHelper.getStrengthSavingThrow(changedTarget);
      if (savingThrow < 12) {
        operationResult.add(eventFactory.successKnockedProneEvent(source, changedTarget));
        operationResult.copyFrom(attackOperation.invoke(source, actionData.getSpecialAttack().getNestedActionData().get(1), changedTarget));
      }
    }
    return operationResult;
  }
}
