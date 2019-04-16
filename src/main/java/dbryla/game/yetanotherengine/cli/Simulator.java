package dbryla.game.yetanotherengine.cli;

import dbryla.game.yetanotherengine.domain.game.Action;
import dbryla.game.yetanotherengine.domain.ai.ArtificialIntelligence;
import dbryla.game.yetanotherengine.domain.game.SubjectTurn;
import dbryla.game.yetanotherengine.domain.game.state.StateMachine;
import dbryla.game.yetanotherengine.domain.game.state.StateMachineFactory;
import dbryla.game.yetanotherengine.domain.game.state.storage.StateStorage;
import dbryla.game.yetanotherengine.domain.operations.ActionData;
import dbryla.game.yetanotherengine.domain.operations.OperationType;
import dbryla.game.yetanotherengine.domain.subject.Abilities;
import dbryla.game.yetanotherengine.domain.subject.CharacterClass;
import dbryla.game.yetanotherengine.domain.subject.Race;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.subject.equipment.Armor;
import dbryla.game.yetanotherengine.domain.subject.equipment.Weapon;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Profile("cli")
class Simulator {

  private final StateStorage stateStorage;
  private final ArtificialIntelligence artificialIntelligence;
  private final StateMachineFactory stateMachineFactory;
  private final ConsolePresenter presenter;

  void start() {
    long gameId = 123L;
    final String player1 = "Clemens";
    final String player2 = "Maria";
    final String blueTeam = "blue";
    Abilities defaultAbilities = new Abilities(10, 10, 10, 10, 10, 10);
    stateStorage.save(gameId, Subject.builder()
        .name(player1)
        .race(Race.HIGH_ELF)
        .affiliation(blueTeam)
        .weapon(Weapon.CLUB)
        .abilities(defaultAbilities)
        .characterClass(CharacterClass.CLERIC)
        .armor(Armor.CHAIN_SHIRT)
        .shield(Armor.SHIELD)
        .build());
    stateStorage.save(gameId, Subject.builder()
        .name(player2)
        .affiliation(blueTeam)
        .race(Race.HALF_ELF)
        .weapon(Weapon.LONGBOW)
        .characterClass(CharacterClass.FIGHTER)
        .abilities(defaultAbilities)
        .build());
    final String enemy = "Borg";
    String greenTeam = "green";
    Subject enemyFighter = Subject.builder()
        .name(enemy)
        .affiliation(greenTeam)
        .race(Race.HALF_ORC)
        .healthPoints(20)
        .abilities(defaultAbilities)
        .weapon(Weapon.GREATSWORD)
        .build();
    stateStorage.save(gameId, enemyFighter);
    artificialIntelligence.initSubject(gameId, enemyFighter);

    StateMachine stateMachine = stateMachineFactory.createInMemoryStateMachine(gameId);
    presenter.showStatus(gameId);
    while (!stateMachine.isInTerminalState()) {
      stateMachine.getNextSubject().ifPresent(subject -> {
            switch (subject.getName()) {
              case player1:
                stateMachine.execute(SubjectTurn.of(new Action(player1, enemy, OperationType.ATTACK, new ActionData(Weapon.CLUB))));
                break;
              case player2:
                stateMachine.execute(SubjectTurn.of(new Action(player2, enemy, OperationType.ATTACK, new ActionData(Weapon.LONGBOW))));
                break;
              case enemy:
                stateMachine.execute(artificialIntelligence.action(enemy));
            }
          }
      );
    }
    presenter.showStatus(gameId);
  }

}
