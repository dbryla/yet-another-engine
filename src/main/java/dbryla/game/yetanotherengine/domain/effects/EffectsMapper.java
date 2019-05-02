package dbryla.game.yetanotherengine.domain.effects;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EffectsMapper {

  private Map<Effect, EffectLogic> map = new HashMap<>();

  public EffectsMapper(BlessEffect blessEffect, BlindEffect blindEffect, LuckyEffect luckyEffect, MultiAttackEffect multiAttackEffect) {
    this.map.put(Effect.BLESS, blessEffect);
    this.map.put(Effect.BLIND, blindEffect);
    this.map.put(Effect.LUCKY, luckyEffect);
    this.map.put(Effect.MULTI_ATTACK, multiAttackEffect);
  }

  public EffectLogic getLogic(Effect effect) {
    return map.get(effect);
  }
}
