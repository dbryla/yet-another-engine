package dbryla.game.yetanotherengine;

import dbryla.game.yetanotherengine.domain.Action;
import dbryla.game.yetanotherengine.domain.IncorrectStateException;
import dbryla.game.yetanotherengine.domain.ai.ArtificialIntelligence;
import dbryla.game.yetanotherengine.domain.operations.AttackOperation;
import dbryla.game.yetanotherengine.domain.operations.Operation;
import dbryla.game.yetanotherengine.domain.operations.SpellCastOperation;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.state.StateMachine;
import dbryla.game.yetanotherengine.domain.state.StateMachineFactory;
import dbryla.game.yetanotherengine.domain.state.storage.StateStorage;
import dbryla.game.yetanotherengine.domain.subjects.IncorrectAttributesException;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import dbryla.game.yetanotherengine.domain.subjects.Weapon;
import dbryla.game.yetanotherengine.domain.subjects.classes.Fighter;
import dbryla.game.yetanotherengine.domain.subjects.classes.Mage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("cli")
public class Cli implements CommandLineRunner {

  private static final String PLAYER = "player";
  private static final String ENEMIES = "enemies";
  private static final String ENEMY1 = "grey goblin";
  private static final String ENEMY2 = "green goblin";
  private final StateStorage stateStorage;
  private final StateMachineFactory stateMachineFactory;
  private final ArtificialIntelligence ai;
  private final Presenter presenter;
  private final Random random = new Random();
  private final Operation attackOperation = new AttackOperation(System.out::println);
  private final Operation spellCastOperation = new SpellCastOperation(System.out::println);
  private String playerName;

  public Cli(StateStorage stateStorage, StateMachineFactory stateMachineFactory, Presenter presenter) {
    this.stateStorage = stateStorage;
    this.stateMachineFactory = stateMachineFactory;
    this.presenter = presenter;
    ai = new ArtificialIntelligence(this.stateStorage, System.out::println);
  }

  @Override
  public void run(String... args) throws Exception {
    switch (args[0]) {
      case "sim":
        simulation();
        break;
      case "game":
        game();
        break;
    }

  }

  private void simulation() {
    System.out.println("Starting simulation...");
    final String player1 = "Clemens";
    final String player2 = "Maria";
    final String blueTeam = "blue";
    stateStorage.save(new Fighter(player1, blueTeam));
    stateStorage.save(new Fighter(player2, blueTeam));
    final String greenTeam = "green";
    final String enemy = "Borg";
    Fighter enemyFighter = Fighter.builder()
        .name(enemy)
        .affiliation(greenTeam)
        .healthPoints(30)
        .build();
    stateStorage.save(enemyFighter);
    ai.initSubject(enemyFighter);
    StateMachine stateMachine = stateMachineFactory
        .createInMemoryStateMachine(subject -> random.nextInt(10));
    while (!stateMachine.isInTerminalState()) {
      presenter.showStatus();
      stateMachine.getNextSubject().ifPresent(subject -> {
            switch (subject.getName()) {
              case player1:
                stateMachine.execute(new Action(player1, enemy, attackOperation));
                break;
              case player2:
                stateMachine.execute(new Action(player2, enemy, attackOperation));
                break;
              case enemy:
                stateMachine.execute(ai.attackAction(enemy));
            }
          }
      );
    }
    presenter.showStatus();
  }

  private void game() throws IOException {
    System.out.println("Starting game mode...");
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    createPlayer(in);
    createEnemies();
    StateMachine stateMachine = stateMachineFactory
        .createInMemoryStateMachine(subject -> random.nextInt(20));
    while (!stateMachine.isInTerminalState()) {
      stateMachine.getNextSubject().ifPresent(subject -> {
            if (playerName.equals(subject.getName())) {
              presenter.showStatus();
              stateMachine.execute(defineAction(in, subject));
            } else {
              stateMachine.execute(ai.attackAction(subject.getName()));
            }
          }
      );
    }
    System.out.println("The end.");
  }

  private Action defineAction(BufferedReader in, Subject subject) {
    if (subject instanceof Mage) {
      System.out.println("Which action you pick: (1) spell, (2) attack");
      String option = readCmdLine(in);
      if (option.equals("1")) {
        return castSpellAction(in, (Mage) subject);
      }
    }
    return new Action(playerName, pickTarget(in), attackOperation);

  }

  private Action castSpellAction(BufferedReader in, Mage subject) {
    if (Spell.FIRE_BOLT.equals(subject.getSpell())) {
      return new Action(playerName, pickTarget(in), spellCastOperation);
    }
    return new Action(playerName, List.of(ENEMY1, ENEMY2), spellCastOperation);
  }

  private String pickTarget(BufferedReader in) {
    System.out.println("Which enemy you want to attack: (1) grey goblin, (2) green goblin");
    String target = readCmdLine(in);
    switch (target) {
      case "1":
        return ENEMY1;
      case "2":
        return ENEMY2;
    }
    throw new IncorrectStateException("Wrong option");
  }

  private void createPlayer(BufferedReader in) throws IOException {
    System.out.println("Type your character name and press enter to start.");
    playerName = in.readLine();
    Subject player = chooseClass(in);
    stateStorage.save(player);
  }

  private Subject chooseClass(BufferedReader in) throws IOException {
    System.out.println("Choose your class: (1) fighter, (2) mage");
    String playerClass = in.readLine();
    switch (playerClass) {
      case "1":
        return buildFighter(in);
      case "2":
        return buildMage(in);
    }
    throw new IncorrectStateException("Wrong option");
  }

  private Fighter buildFighter(BufferedReader in) throws IOException {
    Fighter.Builder builder = Fighter.builder()
        .name(playerName)
        .affiliation(PLAYER);
    System.out.println("Choose your weapon: (1) shortsword and shield, (2) greatsword");
    String weapon = in.readLine();
    switch (weapon) {
      case "1":
        return builder
            .armorClass(12)
            .weapon(Weapon.SHORTSWORD)
            .build();
      case "2":
        return builder
            .weapon(Weapon.GREATSWORD)
            .build();
    }
    throw new IncorrectStateException("Wrong option");
  }

  private Mage buildMage(BufferedReader in) throws IOException {
    Mage.Builder builder = Mage.builder()
        .name(playerName)
        .affiliation(PLAYER);
    System.out.println("Choose your weapon: (1) dagger, (2) quarterstaff");
    String weapon = in.readLine();
    switch (weapon) {
      case "1":
        builder
            .weapon(Weapon.DAGGER);
        break;
      case "2":
        builder
            .weapon(Weapon.QUARTERSTAFF);
        break;
      default:
        throw new IncorrectStateException("Wrong option");
    }
    System.out.println("Choose your spell: (1) fire bolt, (2) color spray");
    String spell = in.readLine();
    switch (spell) {
      case "1":
        return builder
            .spell(Spell.FIRE_BOLT)
            .build();
      case "2":
        return builder
            .spell(Spell.COLOR_SPRAY)
            .build();
    }
    throw new IncorrectStateException("Wrong option");
  }

  private void createEnemies() throws IncorrectAttributesException {
    Fighter enemy1 = Fighter.builder()
        .name(ENEMY1)
        .affiliation(ENEMIES)
        .healthPoints(5)
        .weapon(Weapon.SHORTSWORD)
        .build();
    stateStorage.save(enemy1);
    ai.initSubject(enemy1);
    Fighter enemy2 = Fighter.builder()
        .name(ENEMY2)
        .affiliation(ENEMIES)
        .healthPoints(5)
        .weapon(Weapon.DAGGER)
        .build();
    stateStorage.save(enemy2);
    ai.initSubject(enemy2);
  }

  private String readCmdLine(BufferedReader in) {
    try {
      return in.readLine();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
