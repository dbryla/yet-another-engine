package dbryla.game.yetanotherengine.telegram.callback;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static dbryla.game.yetanotherengine.telegram.CommunicateText.*;

@Component
public class CallbackFactory {

  private final Map<String, CallbackHandler> callbackHandlers = new HashMap<>();
  private final CharacterCreationCallbackHandler characterCreationCallbackHandler;

  public CallbackFactory(ClassCallbackHandler classCallbackHandler,
                         RaceCallbackHandler raceCallbackHandler,
                         AbilitiesCallbackHandler abilitiesCallbackHandler,
                         CharacterCreationCallbackHandler characterCreationCallbackHandler,
                         SpellCallbackHandler spellCallbackHandler,
                         WeaponCallbackHandler weaponCallbackHandler,
                         TargetsCallbackHandler targetsCallbackHandler,
                         MoveCallbackHandler moveCallbackHandler) {
    this.characterCreationCallbackHandler = characterCreationCallbackHandler;
    this.callbackHandlers.put(CLASS, classCallbackHandler);
    this.callbackHandlers.put(RACE, raceCallbackHandler);
    this.callbackHandlers.put(ABILITIES, abilitiesCallbackHandler);
    this.callbackHandlers.put(SPELL, spellCallbackHandler);
    this.callbackHandlers.put(WEAPON, weaponCallbackHandler);
    this.callbackHandlers.put(TARGETS, targetsCallbackHandler);
    this.callbackHandlers.put(MOVE, moveCallbackHandler);
  }

  CallbackHandler getCallbackHandler(String messageText) {
    return callbackHandlers.getOrDefault(messageText, characterCreationCallbackHandler);
  }
}
