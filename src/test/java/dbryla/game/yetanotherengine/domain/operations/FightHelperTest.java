package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.TestData;
import dbryla.game.yetanotherengine.domain.dice.AdvantageRollModifier;
import dbryla.game.yetanotherengine.domain.dice.DiceRollService;
import dbryla.game.yetanotherengine.domain.dice.HitDiceRollModifier;
import dbryla.game.yetanotherengine.domain.effects.Effect;
import dbryla.game.yetanotherengine.domain.effects.FightEffectLogic;
import dbryla.game.yetanotherengine.domain.effects.FightEffectsMapper;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.Condition;
import dbryla.game.yetanotherengine.domain.subject.Race;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static dbryla.game.yetanotherengine.domain.effects.Effect.LUCKY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FightHelperTest {

  @InjectMocks
  private FightHelper fightHelper;

  @Mock
  private DiceRollService diceRollService;

  @Mock
  private FightEffectsMapper fightEffectsMapper;

  @Mock
  private AdvantageRollModifier advantageRollModifier;

  @Test
  void shouldReturnHitRollIfNoEffectIsActive() {
    Subject source = mock(Subject.class);
    Subject target = mock(Subject.class);
    when(source.getConditions()).thenReturn(Set.of());
    when(source.getRace()).thenReturn(Race.HALF_ELF);
    when(diceRollService.k20()).thenReturn(13);

    HitRoll result = fightHelper.getHitRoll(source, Weapon.SHORTSWORD, target);

    assertThat(result.getActual()).isEqualTo(13);
  }

  @Test
  void shouldAddSourceModifierIfEffectIsActiveOnIt() {
    HitDiceRollModifier hitDiceRollModifier = mock(HitDiceRollModifier.class);
    when(hitDiceRollModifier.apply(anyInt())).thenReturn(100);
    Effect effect = mock(Effect.class);
    FightEffectLogic fightEffectLogic = mock(FightEffectLogic.class);
    when(fightEffectsMapper.getLogic(eq(effect))).thenReturn(fightEffectLogic);
    when(fightEffectLogic.getSourceHitRollModifier()).thenReturn(hitDiceRollModifier);
    Subject source = mock(Subject.class);
    when(source.getRace()).thenReturn(Race.HALF_ELF);
    when(source.getConditions()).thenReturn(Set.of(new Condition(effect, 1)));
    Subject target = mock(Subject.class);

    HitRoll result = fightHelper.getHitRoll(source, Weapon.SHORTSWORD, target);

    assertThat(result.getActual()).isEqualTo(100);
  }

  @Test
  void shouldAddTargetModifierIfEffectIsActiveOnIt() {
    HitDiceRollModifier hitDiceRollModifier = mock(HitDiceRollModifier.class);
    when(hitDiceRollModifier.apply(anyInt())).thenReturn(100);
    Effect effect = mock(Effect.class);
    FightEffectLogic fightEffectLogic = mock(FightEffectLogic.class);
    when(fightEffectsMapper.getLogic(eq(effect))).thenReturn(fightEffectLogic);
    when(fightEffectLogic.getTargetHitRollModifier(any())).thenReturn(hitDiceRollModifier);
    Subject source = mock(Subject.class);
    when(source.getRace()).thenReturn(Race.HALF_ELF);
    when(source.getConditions()).thenReturn(Set.of());
    Subject target = mock(Subject.class);
    when(target.getConditions()).thenReturn(Set.of(new Condition(effect, 1)));

    HitRoll result = fightHelper.getHitRoll(source, Weapon.SHORTSWORD, target);

    assertThat(result.getActual()).isGreaterThan(0);
  }

  @Test
  void shouldGetUnchangedAttackDamageIfDidNotRollTwenty() {
    int attackDamage = 10;

    int result = fightHelper.getAttackDamage(null, () -> attackDamage, HitResult.HIT);

    assertThat(result).isEqualTo(attackDamage);
  }

  @Test
  void shouldGetDoubledAttackDamageIfRollTwenty() {
    int attackDamage = 10;
    Subject source = mock(Subject.class);
    when(source.getRace()).thenReturn(Race.HUMAN);

    int result = fightHelper.getAttackDamage(source, () -> attackDamage, HitResult.CRITICAL);

    assertThat(result).isEqualTo(2 * attackDamage);
  }

  @Test
  void shouldReRollOneIfHasLuckyEffect() {
    Subject source = mock(Subject.class);
    when(source.getRace()).thenReturn(Race.LIGHTFOOT_HALFLING);
    Subject target = mock(Subject.class);
    FightEffectLogic fightEffectLogic = mock(FightEffectLogic.class);
    when(fightEffectsMapper.getLogic(LUCKY)).thenReturn(fightEffectLogic);
    HitDiceRollModifier hitRollModifier = mock(HitDiceRollModifier.class);
    when(fightEffectLogic.getSourceHitRollModifier()).thenReturn(hitRollModifier);
    when(hitRollModifier.apply(anyInt())).thenReturn(10);

    HitRoll hitRoll = fightHelper.getHitRoll(source, Weapon.SHORTSWORD, target);

    assertThat(hitRoll.getOriginal()).isEqualTo(10);
  }

  @Test
  void shouldLeaveOrcWithOneHealthPointWhenReducedToZero() {
    Subject target = mock(Subject.class);
    when(target.getRace()).thenReturn(Race.HALF_ORC);
    when(target.getCurrentHealthPoints()).thenReturn(10);
    Subject changedTarget = mock(Subject.class);
    when(target.of(eq(1))).thenReturn(changedTarget);

    Optional<Subject> resultTarget = fightHelper.dealDamage(target, 10, DamageType.SLASHING);

    assertThat(resultTarget).isPresent();
    assertThat(resultTarget.get()).isEqualTo(changedTarget);
  }

  @Test
  void shouldHadAdvantageAgainstPoisonAttackIfTargetIsDwarf() {
    Subject target = mock(Subject.class);
    when(target.getRace()).thenReturn(Race.HILL_DWARF);
    when(target.getAbilities()).thenReturn(TestData.ABILITIES);

    fightHelper.getConstitutionSavingThrow(target, Spell.POISON_SPRAY);

    verify(advantageRollModifier).apply(anyInt());
  }

  @Test
  void shouldPoisonDamageBeDividedByHalfIfTargetIsDwarf() {
    Subject target = mock(Subject.class);
    when(target.getRace()).thenReturn(Race.HILL_DWARF);
    when(target.getCurrentHealthPoints()).thenReturn(10);
    when(target.of(anyInt())).thenReturn(target);

    fightHelper.dealDamage(target, 10, DamageType.POISON);

    verify(target).of(5);
  }

}