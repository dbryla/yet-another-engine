package dbryla.game.yetanotherengine.domain.effects;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FightEffectsMapper {

  private Map<Effect, FightEffectLogic> map = new HashMap<>();

  public FightEffectsMapper(
      BlessFightEffect blessEffect,
      DisadvantageFightEffect disadvantageFightEffect,
      LuckyFightEffect luckyEffect,
      MultiAttackFightEffect multiAttackEffect,
      InvisibleFightEffect invisibleFightEffect,
      AdvantageAgainstTargetFightEffect advantageAgainstTargetFightEffect,
      PetrifiedFightEffect petrifiedFightEffect,
      PoisonedFightEffect poisonedFightEffect,
      ProneFightEffect proneFightEffect) {
    this.map.put(Effect.BLESSED, blessEffect);
    this.map.put(Effect.BLINDED, disadvantageFightEffect);
    this.map.put(Effect.LUCKY, luckyEffect);
    this.map.put(Effect.MULTI_ATTACK, multiAttackEffect);
    this.map.put(Effect.INVISIBLE, invisibleFightEffect);
    this.map.put(Effect.PARALYZED, advantageAgainstTargetFightEffect);
    this.map.put(Effect.PETRIFIED, petrifiedFightEffect);
    this.map.put(Effect.POISONED, poisonedFightEffect);
    this.map.put(Effect.PRONE, proneFightEffect);
    this.map.put(Effect.RESTRAINED, disadvantageFightEffect);
    this.map.put(Effect.STUNNED, advantageAgainstTargetFightEffect);
    this.map.put(Effect.UNCONSCIOUS, advantageAgainstTargetFightEffect);
  }

  public FightEffectLogic getLogic(Effect effect) {
    return map.get(effect);
  }
}
