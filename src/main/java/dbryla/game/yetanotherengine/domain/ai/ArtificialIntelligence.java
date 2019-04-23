package dbryla.game.yetanotherengine.domain.ai;

import dbryla.game.yetanotherengine.domain.IncorrectStateException;
import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.game.Action;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.game.SubjectTurn;
import dbryla.game.yetanotherengine.domain.operations.ActionData;
import dbryla.game.yetanotherengine.domain.operations.OperationType;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import org.springframework.stereotype.Component;

import java.util.*;

import static dbryla.game.yetanotherengine.domain.battleground.Position.ENEMIES_BACK;
import static dbryla.game.yetanotherengine.domain.battleground.Position.PLAYERS_BACK;

@Component
public class ArtificialIntelligence {

  private final Map<String, ArtificialIntelligenceContext> contexts = new HashMap<>();
  private final Random random;
  private final PositionService positionService;

  public ArtificialIntelligence(Random random, PositionService positionService) {
    this.random = random;
    this.positionService = positionService;
  }

  public void initSubject(Game game, Subject subject) {
    contexts.put(subject.getName(), new ArtificialIntelligenceContext(game));
  }

  public SubjectTurn action(String subjectName) {
    verifySubjectIsInitialized(subjectName);
    ArtificialIntelligenceContext context = contexts.get(subjectName);
    Game game = context.getGame();
    Subject subject = game.getSubject(subjectName);
    return getAction(subject, context, false);
  }

  private void verifySubjectIsInitialized(String subjectName) {
    if (!contexts.containsKey(subjectName)) {
      throw new IncorrectStateException("Subject " + subjectName + " isn't initialized with AI.");
    }
  }

  private SubjectTurn getAction(Subject subject, ArtificialIntelligenceContext context, boolean isMoving) {
    return spellAction(subject, context, isMoving)
        .or(() -> attackAction(subject, context, isMoving))
        .orElseGet(() -> moveAction(subject, context, isMoving));
  }

  private Optional<SubjectTurn> spellAction(Subject subject, ArtificialIntelligenceContext context, boolean isMoving) {
    Game game = context.getGame();
    List<Spell> spells = game.getAvailableSpellsForCast(subject);
    List<String> allies = game.getAllAliveAllyNames(subject);
    if (spells != null && !spells.isEmpty()) {
      if (spells.contains(Spell.HEALING_WORD)) {
        List<String> possibleTargets = game.getPossibleTargets(subject, Spell.HEALING_WORD);
        for (String ally : possibleTargets) {
          if (game.getSubject(ally).getSubjectState().needsHealing()) {
            return Optional.of(SubjectTurn.of(new Action(subject.getName(), ally,
                OperationType.SPELL_CAST, new ActionData(Spell.HEALING_WORD))));
          }
        }
      }
      List<String> possibleTargets = game.getPossibleTargets(subject, Spell.BLESS);
      if (spells.contains(Spell.BLESS) && allies.size() > 1 && possibleTargets.size() > 1 && subject.getActiveEffects().isEmpty()) {
        return Optional.of(
            SubjectTurn.of(
                new Action(subject.getName(), possibleTargets, OperationType.SPELL_CAST, new ActionData(Spell.BLESS))));
      }
      Spell spell = Spell.SACRED_FLAME;
      if (spells.contains(spell)) {
        possibleTargets = context.getGame().getPossibleTargets(subject, spell);
        SubjectTurn turn = new SubjectTurn(subject.getName());
        return findTarget(subject, context, turn, possibleTargets, spell.getMinRange(), spell.getMaxRange(), isMoving)
            .map(target -> turn.add(new Action(subject.getName(), target, OperationType.SPELL_CAST, new ActionData(spell))));

      }
    }
    return Optional.empty();
  }

  private Optional<String> findTarget(Subject subject, ArtificialIntelligenceContext context, SubjectTurn subjectTurn,
                                      List<String> possibleTargets, int minRange, int maxRange, boolean isMoving) {
    if (context.getAcquiredTarget() == null && !possibleTargets.isEmpty()) {
      context.setAcquiredTarget(possibleTargets.get(random.nextInt(possibleTargets.size())));
      return Optional.of(context.getAcquiredTarget());
    }
    if (context.getAcquiredTarget() != null && possibleTargets.contains(context.getAcquiredTarget())) {
      return Optional.of(context.getAcquiredTarget());
    }
    if (context.getAcquiredTarget() != null && !possibleTargets.contains(context.getAcquiredTarget())) {
      Subject target = context.getGame().getSubject(context.getAcquiredTarget());
      if (!target.isTerminated()) {
        Optional<Action> action = tryToMove(subject, context, minRange, maxRange, target, isMoving);
        if (action.isPresent()) {
          subjectTurn.add(action.get());
          return Optional.of(context.getAcquiredTarget());
        }
      }
      acquireNewTargetIfPossible(context, possibleTargets);
    }
    return Optional.ofNullable(context.getAcquiredTarget());
  }

  private void acquireNewTargetIfPossible(ArtificialIntelligenceContext context, List<String> possibleTargets) {
    if (!possibleTargets.isEmpty()) {
      context.setAcquiredTarget(possibleTargets.get(random.nextInt(possibleTargets.size())));
    } else {
      context.setAcquiredTarget(null);
    }
  }

  private Optional<Action> tryToMove(Subject subject, ArtificialIntelligenceContext context, int minRange, int maxRange,
                                     Subject target, boolean isMoving) {
    if (isMoving) {
      return Optional.empty();
    }
    return positionService.adjustPosition(subject, target, context.getGame(), minRange, maxRange);
  }

  private Optional<SubjectTurn> attackAction(Subject subject, ArtificialIntelligenceContext context, boolean isMoving) {
    Game game = context.getGame();
    Weapon equippedWeapon = subject.getEquippedWeapon();
    return attackWithWeapon(subject, context, equippedWeapon, isMoving)
        .or(() -> attackWithOtherWeapon(subject, context, game, isMoving));
  }

  private Optional<SubjectTurn> attackWithWeapon(Subject subject, ArtificialIntelligenceContext context,
                                                 Weapon equippedWeapon, boolean isMoving) {
    SubjectTurn turn = new SubjectTurn(subject.getName());
    List<String> possibleTargets = context.getGame().getPossibleTargets(subject, equippedWeapon);
    return findTarget(subject, context, turn, possibleTargets, equippedWeapon.getMinRange(), equippedWeapon.getMaxRange(), isMoving)
        .map(target -> turn.add(new Action(subject.getName(), target, OperationType.ATTACK, new ActionData(equippedWeapon))));
  }

  private Optional<SubjectTurn> attackWithOtherWeapon(Subject subject, ArtificialIntelligenceContext context, Game game, boolean isMoving) {
    List<Weapon> weapons = game.getAvailableWeaponsForAttack(subject);
    if (weapons.isEmpty()) {
      return Optional.empty();
    }
    Weapon weapon = weapons.get(random.nextInt(weapons.size()));
    return attackWithWeapon(subject, context, weapon, isMoving);
  }

  private SubjectTurn moveAction(Subject subject, ArtificialIntelligenceContext context, boolean isMoving) {
    if (!isMoving) {
      Game game = context.getGame();
      int newBattlegroundPosition = subject.getPosition().getBattlegroundLocation() - 1;
      if (newBattlegroundPosition >= PLAYERS_BACK.getBattlegroundLocation()
          && !positionService.wouldNeedToPassEnemies(subject, 1, game)) {
        Position newPosition = Position.valueOf(newBattlegroundPosition);
        Subject changedSubject = subject.of(newPosition);
        List<Action> actions = getAction(changedSubject, context, true).getActions();
        if (actions.size() == 1 && actions.get(0) != null) {
          return new SubjectTurn(subject.getName())
              .add(new Action(subject.getName(), OperationType.MOVE, new ActionData(newPosition)))
              .add(actions.get(0));
        }
      }
      newBattlegroundPosition = subject.getPosition().getBattlegroundLocation() + 1;
      if (newBattlegroundPosition <= Position.ENEMIES_BACK.getBattlegroundLocation()
          && !positionService.wouldNeedToPassEnemies(subject, -1, game)) {
        Position newPosition = Position.valueOf(newBattlegroundPosition);
        Subject changedSubject = subject.of(newPosition);
        List<Action> actions = getAction(changedSubject, context, true).getActions();
        if (actions.size() == 1 && actions.get(0) != null) {
          return new SubjectTurn(subject.getName())
              .add(new Action(subject.getName(), OperationType.MOVE, new ActionData(newPosition)))
              .add(actions.get(0));
        }
      }
      newBattlegroundPosition = subject.getPosition().getBattlegroundLocation() + (subject.getAffiliation().getDirection() * 2);
      if (newBattlegroundPosition >= PLAYERS_BACK.getBattlegroundLocation()
          && newBattlegroundPosition <= ENEMIES_BACK.getBattlegroundLocation()
          && !game.areEnemiesOnCurrentPosition(subject)) {
        return SubjectTurn.of(
            new Action(subject.getName(), OperationType.MOVE, new ActionData(Position.valueOf(newBattlegroundPosition))));
      }
    }
    return new SubjectTurn(subject.getName());
  }

}
