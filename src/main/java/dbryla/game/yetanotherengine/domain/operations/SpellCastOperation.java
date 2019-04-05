package dbryla.game.yetanotherengine.domain.operations;

import static dbryla.game.yetanotherengine.domain.spells.SpellConstants.ALL_ENEMIES;
import static dbryla.game.yetanotherengine.domain.spells.SpellConstants.DAMAGE;
import static dbryla.game.yetanotherengine.domain.spells.SpellConstants.EFFECT;
import static dbryla.game.yetanotherengine.domain.spells.SpellConstants.IRRESISTIBLE;
import static dbryla.game.yetanotherengine.domain.spells.SpellConstants.SPELL_ATTACK;

import dbryla.game.yetanotherengine.domain.events.EventHub;
import dbryla.game.yetanotherengine.domain.events.EventsFactory;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.domain.subjects.classes.Mage;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component("spellCastOperation")
public class SpellCastOperation implements Operation<Mage, Subject> {

  private final EventHub eventHub;
  private final HitRollSupplier hitRollSupplier;
  private final EffectConsumer effectConsumer;
  private final EventsFactory eventsFactory;

  @Override
  public Set<Subject> invoke(Mage source, Subject... targets) throws UnsupportedGameOperationException {
    Spell spell = source.getSpell();
    verifyTargetsNumber(targets, spell);
    switch (spell.getDamageType()) {
      case DAMAGE:
        return tryToHitTargets(targets, source, target -> target.of(target.getHealthPoints() - spell.attackDamageRoll()));
      case EFFECT:
        return tryToHitTargets(targets, source, target -> target.of(spell.getSpellEffect()));
    }
    throw new UnsupportedSpellCastException("Unsupported damage type for spell " + spell);
  }

  private void verifyTargetsNumber(Subject[] targets, Spell spell) throws UnsupportedSpellCastException {
    if (!unlimitedTargets(spell) && spell.getNumberOfTargets() < targets.length) {
      throw new UnsupportedSpellCastException("Can't invoke spell " + spell + " on " + targets.length + " targets.");
    }
  }

  private boolean unlimitedTargets(Spell spell) {
    return spell.getNumberOfTargets() == ALL_ENEMIES;
  }

  private Set<Subject> tryToHitTargets(Subject[] targets, Mage source, Function<Subject, Subject> spellCast) {
    Set<Subject> changes = new HashSet<>();
    switch (source.getSpell().getSpellSaveType()) {
      case SPELL_ATTACK:
        for (Subject target : targets) {
          int hitRoll = hitRollSupplier.get(source, target);
          if (hitRoll >= target.getArmorClass()) {
            changes.add(successSpellCast(source, spellCast, target));
          } else {
            eventHub.send(eventsFactory.failEvent(source.getName(), target.getName()));
          }
        }
        break;
      case IRRESISTIBLE:
        for (Subject target : targets) {
          changes.add(successSpellCast(source, spellCast, target));
        }
    }
    effectConsumer.apply(source).ifPresent(changes::add);
    return changes;
  }

  private Subject successSpellCast(Mage source, Function<Subject, Subject> spellCast, Subject target) {
    Subject changedTarget = spellCast.apply(target);
    eventHub.send(eventsFactory.successSpellCastEvent(source.getName(), changedTarget.getName(), changedTarget.getHealthPoints() <= 0, source.getSpell()));
    return changedTarget;
  }

  @Override
  public int getAllowedNumberOfTargets(Mage source) {
    return source.getSpell().getNumberOfTargets();
  }
}
