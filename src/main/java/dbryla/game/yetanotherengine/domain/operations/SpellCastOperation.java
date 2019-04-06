package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.events.EventsFactory;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subjects.classes.SpellCaster;
import dbryla.game.yetanotherengine.domain.subjects.classes.Subject;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import static dbryla.game.yetanotherengine.domain.spells.SpellConstants.*;

@AllArgsConstructor
@Component("spellCastOperation")
public class SpellCastOperation implements Operation<SpellCaster, Subject> {

  private final EventHub eventHub;
  private final FightHelper fightHelper;
  private final EffectConsumer effectConsumer;
  private final EventsFactory eventsFactory;

  @Override
  public Set<Subject> invoke(SpellCaster source, Subject... targets) throws UnsupportedGameOperationException {
    Spell spell = source.getSpell();
    verifyTargetsNumber(targets, spell);
    Set<Subject> changes = new HashSet<>();
    if (DAMAGE.equals(spell.getDamageType()) && SPELL_ATTACK.equals(spell.getSpellSaveType())) {
      for (Subject target : targets) {
        int hitRoll = fightHelper.getHitRoll(source, target);
        if (fightHelper.isMiss(target.getArmorClass(), hitRoll)) {
          eventHub.send(eventsFactory.failEvent(source, target));
        } else {
          int attackDamage = fightHelper.getAttackDamage(spell.attackDamageRoll(), hitRoll);
          changes.add(dealDamage(source, target, attackDamage));
        }
      }
    } else if (DAMAGE.equals(spell.getDamageType()) && IRRESISTIBLE.equals(source.getSpell().getSpellSaveType())) {
      int attackDamage = spell.attackDamageRoll();
      for (Subject target : targets) {
        changes.add(dealDamage(source, target, attackDamage));
      }
    } else if (EFFECT.equals(spell.getDamageType()) && SPELL_ATTACK.equals(spell.getSpellSaveType())) {
      for (Subject target : targets) {
        int hitRoll = fightHelper.getHitRoll(source, target);
        if (fightHelper.isMiss(target.getArmorClass(), hitRoll)) {
          eventHub.send(eventsFactory.failEvent(source, target));
        } else {
          changes.add(applyEffect(source, spell, target));
        }
      }
    } else if (IRRESISTIBLE.equals(spell.getSpellSaveType()) && EFFECT.equals(spell.getDamageType())) {
      for (Subject target : targets) {
        changes.add(applyEffect(source, spell, target));
      }
    }
    effectConsumer.apply(source).ifPresent(changes::add);
    return changes;
  }

  private void verifyTargetsNumber(Subject[] targets, Spell spell) throws UnsupportedSpellCastException {
    if (!unlimitedTargets(spell) && spell.getNumberOfTargets() < targets.length) {
      throw new UnsupportedSpellCastException("Can't invoke spell " + spell + " on " + targets.length + " targets.");
    }
  }

  private boolean unlimitedTargets(Spell spell) {
    return spell.getNumberOfTargets() == UNLIMITED_TARGETS;
  }

  private Subject dealDamage(SpellCaster source, Subject target, int attackDamage) {
    int remainingHealthPoints = target.getHealthPoints() - attackDamage;
    Subject changedTarget = target.of(remainingHealthPoints);
    eventHub.send(eventsFactory.successSpellCastEvent(source, changedTarget));
    return changedTarget;
  }

  private Subject applyEffect(SpellCaster source, Spell spell, Subject target) {
    Subject changedTarget = target.of(spell.getSpellEffect());
    eventHub.send(eventsFactory.successSpellCastEvent(source, changedTarget));
    return changedTarget;
  }

  @Override
  public int getAllowedNumberOfTargets(SpellCaster source) {
    return source.getSpell().getNumberOfTargets();
  }
}
