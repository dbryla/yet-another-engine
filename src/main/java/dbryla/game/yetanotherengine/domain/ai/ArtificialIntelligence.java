package dbryla.game.yetanotherengine.domain.ai;

import static dbryla.game.yetanotherengine.domain.GameOptions.ENEMIES;

import dbryla.game.yetanotherengine.domain.Action;
import dbryla.game.yetanotherengine.domain.IncorrectStateException;
import dbryla.game.yetanotherengine.domain.Instrument;
import dbryla.game.yetanotherengine.domain.operations.AttackOperation;
import dbryla.game.yetanotherengine.domain.operations.SpellCastOperation;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.state.storage.StateStorage;
import dbryla.game.yetanotherengine.domain.subjects.Monster;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.stereotype.Component;

@Component
public class ArtificialIntelligence {

  private final Map<String, ArtificialIntelligenceConfiguration> subjects = new HashMap<>();
  private final StateStorage stateStorage;
  private final AttackOperation attackOperation;
  private final SpellCastOperation spellCastOperation;
  private final Random random;

  public ArtificialIntelligence(StateStorage stateStorage, AttackOperation attackOperation,
      SpellCastOperation spellCastOperation, Random random) {
    this.stateStorage = stateStorage;
    this.attackOperation = attackOperation;
    this.spellCastOperation = spellCastOperation;
    this.random = random;
  }

  public void initSubject(Monster subject) {
    subjects.put(subject.getName(), new ArtificialIntelligenceConfiguration(subject));
  }

  public Action action(String subjectName) {
    verifySubjectIsInitialized(subjectName);

    ArtificialIntelligenceConfiguration ai = subjects.get(subjectName);
    setTarget(ai);
    List<Spell> spells = ai.getSubject().getSpells();
    if (spells != null && !spells.isEmpty()) {
      List<Subject> allies = StreamSupport.stream(stateStorage.findAll().spliterator(), false)
          .filter(subject -> ENEMIES.equals(subject.getAffiliation())).collect(Collectors.toList());
      if (spells.contains(Spell.HEALING_WORD)) {
        for (Subject ally : allies) {
          if (ally.getSubjectState().needsHealing()) {
            return new Action(ai.getSubject().getName(), ally.getName(), spellCastOperation, new Instrument(Spell.HEALING_WORD));
          }
        }
      }
      if (spells.contains(Spell.BLESS) && allies.size() > 1 && stateStorage.findByName(subjectName).get().getActiveEffect().isEmpty()) {
        return new Action(ai.getSubject().getName(), allies.stream().map(Subject::getName).collect(Collectors.toList()),
            spellCastOperation, new Instrument(Spell.BLESS));
      }
      if (spells.contains(Spell.SACRED_FLAME)) {
        return new Action(ai.getSubject().getName(), ai.getAcquiredTarget(), spellCastOperation, new Instrument(Spell.SACRED_FLAME));
      }
    }
    return new Action(ai.getSubject().getName(), ai.getAcquiredTarget(), attackOperation,
        new Instrument(ai.getSubject().getEquipment().getWeapon()));
  }

  private void verifySubjectIsInitialized(String subjectName) {
    if (!subjects.containsKey(subjectName)) {
      throw new IncorrectStateException("Subject " + subjectName + " isn't initialized with AI.");
    }
  }

  private void setTarget(ArtificialIntelligenceConfiguration ai) {
    String acquiredTarget = ai.getAcquiredTarget();
    if (acquiredTarget == null) {
      ai.setAcquiredTarget(findNewTarget(ai.getSubject()));
    } else {
      Optional<Subject> target = stateStorage.findByName(acquiredTarget);
      if (target.isEmpty()) {
        throw new IncorrectStateException("State storage is corrupted. Can't find subject: " + acquiredTarget);
      }
      if (target.get().isTerminated()) {
        ai.setAcquiredTarget(findNewTarget(ai.getSubject()));
      }
    }
  }

  private String findNewTarget(Subject subject) {
    List<Subject> targets = StreamSupport.stream(stateStorage.findAll().spliterator(), false)
        .filter(s -> !s.isTerminated() && !s.getAffiliation().equals(subject.getAffiliation())).collect(Collectors.toList());
    int size = targets.size();
    if (size == 0) {
      throw new IncorrectStateException("Target not found for subject: " + subject.getName());
    }
    return targets.get(random.nextInt(size)).getName();
  }

}
