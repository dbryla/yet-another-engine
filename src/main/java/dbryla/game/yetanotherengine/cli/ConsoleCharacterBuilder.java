package dbryla.game.yetanotherengine.cli;

import static dbryla.game.yetanotherengine.domain.GameOptions.PLAYER;

import dbryla.game.yetanotherengine.Presenter;
import dbryla.game.yetanotherengine.domain.IncorrectStateException;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.domain.subjects.Weapon;
import dbryla.game.yetanotherengine.domain.subjects.classes.Fighter;
import dbryla.game.yetanotherengine.domain.subjects.classes.Mage;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ConsoleCharacterBuilder {

  private final Presenter presenter;
  private final ConsoleInputProvider inputProvider;

  Subject createPlayer() {
    System.out.println("Type your character name and press enter to start.");
    String playerName = inputProvider.cmdLine();
    return chooseClass(playerName);
  }

  private Subject chooseClass(String playerName) {
    Map<Integer, Class> availableClasses = presenter.showAvailableClasses();
    int playerChoice = inputProvider.cmdLineToOption();
    Class clazz = availableClasses.get(playerChoice);
    if (Fighter.class.equals(clazz)) {
      return buildFighter(playerName);
    } else if (Mage.class.equals(clazz)) {
      return buildMage(playerName);
    } else {
      throw new IncorrectStateException("Wrong option");
    }
  }

  private Fighter buildFighter(String playerName) {
    Fighter.Builder builder = Fighter.builder()
        .name(playerName)
        .affiliation(PLAYER);
    Map<Integer, Weapon> availableWeapons = presenter.showAvailableWeapons();
    int playerChoice = inputProvider.cmdLineToOption();
    return builder.weapon(availableWeapons.get(playerChoice)).build();
  }

  private Mage buildMage(String playerName) {
    Mage.Builder builder = Mage.builder()
        .name(playerName)
        .affiliation(PLAYER);
    Map<Integer, Weapon> availableWeapons = presenter.showAvailableWeapons();
    int playerChoice = inputProvider.cmdLineToOption();
    builder.weapon(availableWeapons.get(playerChoice));
    Map<Integer, Spell> availableSpells = presenter.showAvailableSpells();
    playerChoice = inputProvider.cmdLineToOption();
    return builder.spell(availableSpells.get(playerChoice)).build();
  }

}
