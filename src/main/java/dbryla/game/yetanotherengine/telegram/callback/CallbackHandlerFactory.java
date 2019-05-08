package dbryla.game.yetanotherengine.telegram.callback;

import static dbryla.game.yetanotherengine.telegram.CommunicateText.ABILITIES;
import static dbryla.game.yetanotherengine.telegram.CommunicateText.ARMOR;
import static dbryla.game.yetanotherengine.telegram.CommunicateText.CLASS;
import static dbryla.game.yetanotherengine.telegram.CommunicateText.EXTRA_ABILITIES;
import static dbryla.game.yetanotherengine.telegram.CommunicateText.MOVE;
import static dbryla.game.yetanotherengine.telegram.CommunicateText.RACE;
import static dbryla.game.yetanotherengine.telegram.CommunicateText.SPELL;
import static dbryla.game.yetanotherengine.telegram.CommunicateText.SPELLS;
import static dbryla.game.yetanotherengine.telegram.CommunicateText.TARGETS;
import static dbryla.game.yetanotherengine.telegram.CommunicateText.WEAPON;
import static dbryla.game.yetanotherengine.telegram.CommunicateText.WEAPONS;

import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("tg")
public class CallbackHandlerFactory {

  private final Map<String, CallbackHandler> callbackHandlers = new HashMap<>();

  public CallbackHandlerFactory(ClassCallbackHandler classCallbackHandler,
      RaceCallbackHandler raceCallbackHandler,
      AbilitiesCallbackHandler abilitiesCallbackHandler,
      SpellCallbackHandler spellCallbackHandler,
      WeaponCallbackHandler weaponCallbackHandler,
      TargetsCallbackHandler targetsCallbackHandler,
      MoveCallbackHandler moveCallbackHandler,
      ExtraAbilitiesCallbackHandler extraAbilitiesCallbackHandler,
      WeaponsCallbackHandler weaponsCallbackHandler,
      SpellsCallbackHandler spellsCallbackHandler,
      ArmorCallbackHandler armorCallbackHandler) {
    this.callbackHandlers.put(CLASS, classCallbackHandler);
    this.callbackHandlers.put(RACE, raceCallbackHandler);
    this.callbackHandlers.put(ABILITIES, abilitiesCallbackHandler);
    this.callbackHandlers.put(SPELL, spellCallbackHandler);
    this.callbackHandlers.put(WEAPON, weaponCallbackHandler);
    this.callbackHandlers.put(TARGETS, targetsCallbackHandler);
    this.callbackHandlers.put(MOVE, moveCallbackHandler);
    this.callbackHandlers.put(EXTRA_ABILITIES, extraAbilitiesCallbackHandler);
    this.callbackHandlers.put(WEAPONS, weaponsCallbackHandler);
    this.callbackHandlers.put(SPELLS, spellsCallbackHandler);
    this.callbackHandlers.put(ARMOR, armorCallbackHandler);
  }

  CallbackHandler getCallbackHandler(String messageText) {
    return callbackHandlers.get(messageText);
  }
}
