package dbryla.game.yetanotherengine.cli;

import dbryla.game.yetanotherengine.domain.ai.ArtificialIntelligence;
import dbryla.game.yetanotherengine.domain.battleground.Position;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.game.GameFactory;
import dbryla.game.yetanotherengine.domain.game.state.StateMachine;
import dbryla.game.yetanotherengine.domain.game.state.StateMachineFactory;
import dbryla.game.yetanotherengine.domain.game.state.storage.StateStorage;
import dbryla.game.yetanotherengine.domain.subject.Abilities;
import dbryla.game.yetanotherengine.domain.subject.CharacterClass;
import dbryla.game.yetanotherengine.domain.subject.Race;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
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

  private final StateStorage stateStorage;
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
    Subject cleric = Subject.builder()
        .name(player1)
        .race(Race.HIGH_ELF)
        .affiliation(PLAYERS)
        .weapon(Weapon.CLUB)
        .abilities(defaultAbilities)
        .characterClass(CharacterClass.CLERIC)
        .position(Position.PLAYERS_FRONT)
        .armor(Armor.CHAIN_SHIRT)
        .shield(Armor.SHIELD)
        .equippedWeapon(Weapon.CLUB)
        .build();
    stateStorage.save(gameId, cleric);
    artificialIntelligence.initSubject(game, cleric);
    Subject fighter = Subject.builder()
        .name(player2)
        .affiliation(PLAYERS)
        .race(Race.HALF_ELF)
        .weapons(List.of(Weapon.LONGBOW, Weapon.DAGGER))
        .equippedWeapon(Weapon.LONGBOW)
        .characterClass(CharacterClass.FIGHTER)
        .position(Position.PLAYERS_BACK)
        .abilities(defaultAbilities)
        .build();
    stateStorage.save(gameId, fighter);
    artificialIntelligence.initSubject(game, fighter);
    final String enemy = "Borg";
    Subject enemyFighter = Subject.builder()
        .name(enemy)
        .affiliation(ENEMIES)
        .race(Race.HALF_ORC)
        .healthPoints(30)
        .abilities(defaultAbilities)
        .weapon(Weapon.GREATSWORD)
        .equippedWeapon(Weapon.GREATSWORD)
        .position(Position.ENEMIES_FRONT)
        .build();
    stateStorage.save(gameId, enemyFighter);
    artificialIntelligence.initSubject(game, enemyFighter);

    StateMachine stateMachine = stateMachineFactory.createInMemoryStateMachine(gameId);
    presenter.showStatus(gameId);
    while (!stateMachine.isInTerminalState()) {
      stateMachine.getNextSubject().ifPresent(subject -> stateMachine.execute(artificialIntelligence.action(subject.getName())));
    }
    presenter.showStatus(gameId);
  }

}
