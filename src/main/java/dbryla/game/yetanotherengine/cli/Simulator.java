package dbryla.game.yetanotherengine.cli;

import dbryla.game.yetanotherengine.domain.ai.ArtificialIntelligence;
import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.game.GameFactory;
import dbryla.game.yetanotherengine.domain.game.state.StateMachine;
import dbryla.game.yetanotherengine.domain.game.state.StateMachineFactory;
import dbryla.game.yetanotherengine.domain.game.state.storage.SubjectStorage;
import dbryla.game.yetanotherengine.domain.subject.Abilities;
import dbryla.game.yetanotherengine.domain.subject.CharacterClass;
import dbryla.game.yetanotherengine.domain.subject.Race;
import dbryla.game.yetanotherengine.domain.subject.State;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.equipment.Armor;
import dbryla.game.yetanotherengine.domain.equipment.Weapon;
import dbryla.game.yetanotherengine.domain.subject.SubjectFactory;
import dbryla.game.yetanotherengine.domain.subject.SubjectProperties;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

import static dbryla.game.yetanotherengine.domain.subject.Affiliation.ENEMIES;
import static dbryla.game.yetanotherengine.domain.subject.Affiliation.PLAYERS;

@Component
@AllArgsConstructor
@Profile("cli")
class Simulator {

  private final SubjectStorage subjectStorage;
  private final SubjectFactory subjectFactory;
  private final ArtificialIntelligence artificialIntelligence;
  private final StateMachineFactory stateMachineFactory;
  private final ConsolePresenter presenter;
  private final GameFactory gameFactory;

  void start() {
    final String player1 = "Clemens";
    final String player2 = "Maria";
    Abilities defaultAbilities = new Abilities(10, 10, 10, 10, 10, 10);
    Long gameId = 123L;
    Game game = gameFactory.newGame(gameId);
    SubjectProperties clericProperties = SubjectProperties.builder()
        .name(player1)
        .race(Race.HIGH_ELF)
        .affiliation(PLAYERS)
        .weapon(Weapon.CLUB)
        .abilities(defaultAbilities)
        .characterClass(CharacterClass.CLERIC)
        .armor(Armor.CHAIN_SHIRT)
        .shield(Armor.SHIELD)
        .build();
    Subject cleric = new Subject(clericProperties,
        new State(player1, clericProperties.getMaxHealthPoints(), clericProperties.getMaxHealthPoints(),
            Position.PLAYERS_FRONT, Set.of(), Weapon.CLUB));
    subjectStorage.save(gameId, cleric);
    artificialIntelligence.initSubject(game, cleric);
    SubjectProperties fighterProperties = SubjectProperties.builder()
        .name(player2)
        .affiliation(PLAYERS)
        .race(Race.HALF_ELF)
        .weapons(List.of(Weapon.LONGBOW, Weapon.DAGGER))
        .characterClass(CharacterClass.FIGHTER)
        .abilities(defaultAbilities)
        .build();
    Subject fighter = new Subject(fighterProperties,
        new State(player2, fighterProperties.getMaxHealthPoints(), fighterProperties.getMaxHealthPoints(),
            Position.PLAYERS_BACK, Set.of(), Weapon.LONGBOW));
    subjectStorage.save(gameId, fighter);
    artificialIntelligence.initSubject(game, fighter);
    final String enemy = "Borg";
    SubjectProperties enemyProperties = SubjectProperties.builder()
        .name(enemy)
        .affiliation(ENEMIES)
        .race(Race.HALF_ORC)
        .healthPoints(30)
        .abilities(defaultAbilities)
        .weapon(Weapon.GREATSWORD)
        .build();
    Subject enemyFighter = new Subject(enemyProperties,
        new State(enemy, enemyProperties.getMaxHealthPoints(), enemyProperties.getMaxHealthPoints(),
            Position.ENEMIES_FRONT, Set.of(), Weapon.GREATSWORD));
    subjectStorage.save(gameId, enemyFighter);
    artificialIntelligence.initSubject(game, enemyFighter);

    StateMachine stateMachine = stateMachineFactory.createInMemoryStateMachine(gameId);
    presenter.showStatus(gameId);
    while (!stateMachine.isInTerminalState()) {
      stateMachine.getNextSubject()
          .ifPresent(subject -> stateMachine.execute(artificialIntelligence.action(subject.getName())));
    }
    presenter.showStatus(gameId);
  }

}
