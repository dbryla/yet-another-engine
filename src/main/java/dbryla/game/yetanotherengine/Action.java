package dbryla.game.yetanotherengine;

import java.util.List;
import java.util.Set;

public interface Action {

  String getSourceName();

  List<String> getTargetNames();

  Set<Subject> invoke(Subject source, Subject... targets);
}
