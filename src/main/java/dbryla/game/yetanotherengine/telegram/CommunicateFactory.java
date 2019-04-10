package dbryla.game.yetanotherengine.telegram;

import dbryla.game.yetanotherengine.domain.AbilityScoresSupplier;
import dbryla.game.yetanotherengine.domain.GameOptions;
import dbryla.game.yetanotherengine.session.Session;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@Component
@Profile("tg")
public class CommunicateFactory {

  private static final String CLASS = "Choose a class:";
  public static final String ABILITIES = "Assign scores to your abilities: Str, Dex, Con, Int, Wis, Cha";
  private static final String WEAPON = "";

  private final GameOptions gameOptions;

  public Communicate chooseClassCommunicate() {
    ArrayList<Class> classes = new ArrayList<>(gameOptions.getAvailableClasses());
    List<InlineKeyboardButton> keyboardButtons = new LinkedList<>();
    classes.forEach(clazz -> keyboardButtons.add(
        new InlineKeyboardButton(clazz.getSimpleName()).setCallbackData(clazz.getSimpleName())));
    return new Communicate(CLASS, keyboardButtons);
  }

  public Communicate assignAbilitiesCommunicate(List<Integer> scores) {
    List<InlineKeyboardButton> keyboardButtons = createKeyboardButtons(scores);
    return new Communicate(ABILITIES, keyboardButtons);
  }

  private List<InlineKeyboardButton> createKeyboardButtons(List<Integer> scores) {
    List<InlineKeyboardButton> keyboardButtons = new LinkedList<>();
    for (Integer integer : scores) {
      String score = String.valueOf(integer);
      keyboardButtons.add(new InlineKeyboardButton().setText(String.valueOf(score)).setCallbackData(score));
    }
    return keyboardButtons;
  }

  public Communicate nextAbilityAssignment(Session session, String lastScore) {
    session.getAbilityScores().remove(Integer.valueOf(lastScore));
    return new Communicate(ABILITIES, createKeyboardButtons(session.getAbilityScores()));
  }

  public Communicate chooseWeaponCommunicate() {
    gameOptions.getAvailableWeapons(null);
    return new Communicate(WEAPON, null);
  }
}
