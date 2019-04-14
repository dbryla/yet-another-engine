package dbryla.game.yetanotherengine.cli;

import dbryla.game.yetanotherengine.domain.subject.Abilities;
import dbryla.game.yetanotherengine.domain.IncorrectStateException;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Profile("cli")
class ConsoleAbilitiesProvider {

  private final ConsolePresenter presenter;
  private final ConsoleInputProvider consoleInputProvider;

  Abilities getAbilities() {
    List<Integer> abilityScores = presenter.showGeneratedAbilityScores();
    return new Abilities(
        getAbilityScore(abilityScores),
        getAbilityScore(abilityScores),
        getAbilityScore(abilityScores),
        getAbilityScore(abilityScores),
        getAbilityScore(abilityScores),
        getAbilityScore(abilityScores)
    );
  }

  private int getAbilityScore(List<Integer> abilityScores) {
    int score = consoleInputProvider.cmdLineToOption();
    boolean removed = abilityScores.remove(Integer.valueOf(score));
    if (!removed) {
      throw new IncorrectStateException("Not existing score: " + score);
    }
    return score;
  }
}