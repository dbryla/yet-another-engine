package dbryla.game.yetanotherengine.cli;

import dbryla.game.yetanotherengine.domain.IncorrectStateException;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.domain.subjects.Weapon;
import dbryla.game.yetanotherengine.domain.subjects.classes.Fighter;
import dbryla.game.yetanotherengine.domain.subjects.classes.Mage;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import static dbryla.game.yetanotherengine.domain.GameOptions.PLAYER;

public class ConsoleCharacterBuilder {

  private final ConsolePresenter presenter;

  private final BufferedReader in;

  public ConsoleCharacterBuilder(ConsolePresenter presenter, BufferedReader in) {
    this.presenter = presenter;
    this.in = in;
  }

  Subject createPlayer() throws IOException {
    System.out.println("Type your character name and press enter to start.");
    String playerName = in.readLine();
    return chooseClass(playerName);
  }

  private Subject chooseClass(String playerName) {
    Map<Integer, Class> availableClasses = presenter.showAvailableClasses();
    int playerChoice = readCmdLineOption(in);
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
    int playerChoice = readCmdLineOption(in);
    return builder.weapon(availableWeapons.get(playerChoice)).build();
  }

  private Mage buildMage(String playerName) {
    Mage.Builder builder = Mage.builder()
        .name(playerName)
        .affiliation(PLAYER);
    Map<Integer, Weapon> availableWeapons = presenter.showAvailableWeapons();
    int playerChoice = readCmdLineOption(in);
    builder.weapon(availableWeapons.get(playerChoice));
    Map<Integer, Spell> availableSpells = presenter.showAvailableSpells();
    playerChoice = readCmdLineOption(in);
    return builder.spell(availableSpells.get(playerChoice)).build();
  }

  int readCmdLineOption(BufferedReader in) {
    try {
      return Integer.valueOf(in.readLine());
    } catch (IOException e) {
      throw new IncorrectStateException("Exception while reading cmdline option.", e);
    }
  }

}
