package dbryla.game.yetanotherengine.telegram;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@AllArgsConstructor
@Getter
public class Communicate {
  private final String text;
  private final List<InlineKeyboardButton> keyboardButtons;
}
