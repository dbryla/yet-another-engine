package dbryla.game.yetanotherengine.telegram;

import static dbryla.game.yetanotherengine.telegram.CommunicateFactory.ABILITIES;
import static dbryla.game.yetanotherengine.telegram.CommunicateFactory.CLASS;

import dbryla.game.yetanotherengine.domain.AbilityScoresSupplier;
import dbryla.game.yetanotherengine.session.Session;
import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SessionFactory {

  private final CommunicateFactory communicateFactory;
  private final AbilityScoresSupplier abilityScoresSupplier;


  public Session createSession(String playerName, Integer messageId) {
    List<Integer> abilityScores = abilityScoresSupplier.get();
    return new Session(playerName, messageId, new LinkedList<>(List.of(communicateFactory.chooseClassCommunicate(),
        communicateFactory.assignAbilitiesCommunicate(abilityScores))), abilityScores);
  }

  public void updateSession(String messageText, Session session, String callbackData) {
    session.update(messageText, callbackData);
    if (messageText.contains(CLASS)) {
      session.addLastCommunicate(communicateFactory.chooseWeaponCommunicate(callbackData));
      communicateFactory.chooseArmorCommunicate(callbackData).ifPresent(session::addLastCommunicate);
    }
    if (messageText.contains(ABILITIES) && session.getAbilityScores().size() > 1) {
      session.addNextCommunicate(
          communicateFactory.nextAbilityAssignment(session, callbackData));
    }
  }

}
