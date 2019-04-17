package dbryla.game.yetanotherengine.domain.ai;

import static dbryla.game.yetanotherengine.domain.game.GameOptions.ENEMIES;

import dbryla.game.yetanotherengine.domain.game.Action;
import dbryla.game.yetanotherengine.domain.IncorrectStateException;
import dbryla.game.yetanotherengine.domain.game.SubjectTurn;
import dbryla.game.yetanotherengine.domain.operations.ActionData;
import dbryla.game.yetanotherengine.domain.operations.OperationType;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.game.state.storage.StateStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import dbryla.game.yetanotherengine.domain.subject.Subject;
import org.springframework.stereotype.Component;

@Component
public class ArtificialIntelligence {

  private final Map<String, ArtificialIntelligenceConfiguration> subjects = new HashMap<>();
  private final StateStorage stateStorage;
  private final Random random;

  public ArtificialIntelligence(StateStorage stateStorage, Random random) {
    this.stateStorage = stateStorage;
    this.random = random;
  }

  public void initSubject(Long gameId, Subject subject) {
    subjects.put(subject.getName(), new ArtificialIntelligenceConfiguration(subject, gameId));
  }

  public SubjectTurn action(String subjectName) {
    verifySubjectIsInitialized(subjectName);

    ArtificialIntelligenceConfiguration ai = subjects.get(subjectName);
    setTarget(ai);
    List<Spell> spells = ai.getSubject().getSpells();
    if (spells != null && !spells.isEmpty()) {
      List<Subject> allies = stateStorage.findAll(ai.getGameId()).stream()
          .filter(subject -> ENEMIES.equals(subject.getAffiliation())).collect(Collectors.toList());
      if (spells.contains(Spell.HEALING_WORD)) {
        for (Subject ally : allies) {
          if (ally.getSubjectState().needsHealing()) {
            return SubjectTurn.of(new Action(ai.getSubject().getName(), ally.getName(),
                OperationType.SPELL_CAST, new ActionData(Spell.HEALING_WORD)));
          }
        }
      }
      if (spells.contains(Spell.BLESS) && allies.size() > 1
          && stateStorage.findByIdAndName(ai.getGameId(), subjectName).get().getActiveEffects().isEmpty()) {
        return SubjectTurn.of(new Action(ai.getSubject().getName(), allies.stream().map(Subject::getName).collect(Collectors.toList()),
            OperationType.SPELL_CAST, new ActionData(Spell.BLESS)));
      }
      if (spells.contains(Spell.SACRED_FLAME)) {
        return SubjectTurn.of(new Action(ai.getSubject().getName(), ai.getAcquiredTarget(),
            OperationType.SPELL_CAST, new ActionData(Spell.SACRED_FLAME)));
      }
    }
    return SubjectTurn.of(new Action(ai.getSubject().getName(), ai.getAcquiredTarget(), OperationType.ATTACK,
        new ActionData(ai.getSubject().getEquipment().getWeapons().get(0))));
  }

  private void verifySubjectIsInitialized(String subjectName) {
    if (!subjects.containsKey(subjectName)) {
      throw new IncorrectStateException("Subject " + subjectName + " isn't initialized with AI.");
    }
  }

  private void setTarget(ArtificialIntelligenceConfiguration ai) {
    String acquiredTarget = ai.getAcquiredTarget();
    if (acquiredTarget == null) {
      ai.setAcquiredTarget(findNewTarget(ai));
    } else {
      Optional<Subject> target = stateStorage.findByIdAndName(ai.getGameId(), acquiredTarget);
      if (target.isEmpty()) {
        throw new IncorrectStateException("State storage is corrupted. Can't find subject: " + acquiredTarget);
      }
      if (target.get().isTerminated()) {
        ai.setAcquiredTarget(findNewTarget(ai));
      }
    }
  }

  private String findNewTarget(ArtificialIntelligenceConfiguration ai) {
    List<Subject> targets = stateStorage.findAll(ai.getGameId()).stream()
        .filter(s -> !s.isTerminated() && !s.getAffiliation().equals(ai.getSubject().getAffiliation())).collect(Collectors.toList());
    int size = targets.size();
    if (size == 0) {
      throw new IncorrectStateException("Target not found for subject: " + ai.getSubject().getName());
    }
    return targets.get(random.nextInt(size)).getName();
  }

}
