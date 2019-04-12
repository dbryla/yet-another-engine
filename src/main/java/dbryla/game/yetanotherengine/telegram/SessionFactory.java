package dbryla.game.yetanotherengine.telegram;

import static dbryla.game.yetanotherengine.telegram.BuildingFactory.ABILITIES;
import static dbryla.game.yetanotherengine.telegram.BuildingFactory.CLASS;

import dbryla.game.yetanotherengine.domain.AbilityScoresSupplier;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.session.Session;
import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("tg")
@AllArgsConstructor
public class SessionFactory {

  private final BuildingFactory buildingFactory;
  private final CharacterBuilder characterBuilder;
  private final AbilityScoresSupplier abilityScoresSupplier;


  public Session createSession(String playerName, Integer messageId) {
    List<Integer> abilityScores = abilityScoresSupplier.get();
    return new Session(playerName, messageId, new LinkedList<>(List.of(buildingFactory.chooseClassCommunicate(),
        buildingFactory.assignAbilitiesCommunicate(abilityScores))), abilityScores);
  }

  public void updateSession(String messageText, Session session, String callbackData) {
    session.update(messageText, callbackData);
    if (messageText.contains(CLASS)) {
      session.addLastCommunicate(buildingFactory.chooseWeaponCommunicate(callbackData));
      buildingFactory.chooseArmorCommunicate(callbackData).ifPresent(session::addLastCommunicate);
    }
    if (messageText.contains(ABILITIES) && session.getAbilityScores().size() > 1) {
      session.addNextCommunicate(
          buildingFactory.nextAbilityAssignment(session, callbackData));
    }
  }

  public Subject createCharacter(Session session) {
    return characterBuilder.create(session);
  }
}
