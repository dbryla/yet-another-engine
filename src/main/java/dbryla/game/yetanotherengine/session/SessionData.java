package dbryla.game.yetanotherengine.session;

import dbryla.game.yetanotherengine.telegram.Communicate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
class SessionData {

  private String playerName;
  private List<Communicate> communicates;
}
