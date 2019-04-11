package dbryla.game.yetanotherengine.telegram;


import dbryla.game.yetanotherengine.InputProvider;
import dbryla.game.yetanotherengine.domain.Action;
import dbryla.game.yetanotherengine.domain.Game;
import dbryla.game.yetanotherengine.domain.subjects.Subject;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("tg")
public class DummyInputProvider implements InputProvider {

  @Override
  public Action askForAction(Subject subject, Game game) {
    return null;
  }
}
