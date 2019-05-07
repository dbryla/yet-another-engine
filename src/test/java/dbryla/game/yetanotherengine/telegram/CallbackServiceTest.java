package dbryla.game.yetanotherengine.telegram;

import dbryla.game.yetanotherengine.db.CharacterRepository;
import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.game.SubjectTurn;
import dbryla.game.yetanotherengine.domain.operations.OperationType;
import dbryla.game.yetanotherengine.domain.spells.Spell;
import dbryla.game.yetanotherengine.domain.subject.SubjectFactory;
import dbryla.game.yetanotherengine.session.Session;
import dbryla.game.yetanotherengine.telegram.callback.CallbackService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static dbryla.game.yetanotherengine.telegram.BuildingFactory.ABILITIES;
import static dbryla.game.yetanotherengine.telegram.BuildingFactory.WEAPONS;
import static dbryla.game.yetanotherengine.telegram.FightFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CallbackServiceTest {

  @InjectMocks
  private CallbackService callbackService;

  @Mock
  private SessionFactory sessionFactory;

  @Mock
  private TelegramClient telegramClient;

  @Mock
  private SubjectFactory subjectFactory;

  @Mock
  private Commons commons;

  @Mock
  private CharacterRepository characterRepository;

  @Mock
  private FightFactory fightFactory;

  @Mock
  private Update update;

  @Mock
  private Session session;

  @Test
  void shouldUpdateSessionWithCallbackData() {
    givenUpdateWithTextAndData("message-text", "callback-data");
    when(sessionFactory.getGameOrCreate(any())).thenReturn(mock(Game.class));

    callbackService.execute(update);

    verify(sessionFactory).updateSession(any(), any(), eq("callback-data"));
  }

  private void givenUpdateWithTextAndData(String text, String callbackData) {
    Message replyMessage = mock(Message.class);
    Message message = mock(Message.class);
    when(message.getReplyToMessage()).thenReturn(replyMessage);
    when(message.getText()).thenReturn(text);
    CallbackQuery callbackQuery = mock(CallbackQuery.class);
    when(callbackQuery.getData()).thenReturn(callbackData);
    when(callbackQuery.getMessage()).thenReturn(message);
    when(update.getCallbackQuery()).thenReturn(callbackQuery);
    when(commons.getCharacterName(any())).thenReturn("player");
    when(sessionFactory.getSession(any())).thenReturn(session);
  }

  @Test
  void shouldEditAbilitiesCommunicateAfterUserInteraction() {
    givenUpdateWithTextAndData(ABILITIES, "callback-data");
    when(session.getNextCommunicate()).thenReturn(Optional.of(new Communicate(ABILITIES, List.of())));

    callbackService.execute(update);

    verify(telegramClient).sendEditKeyboard(any(), any(), any());
  }

  @Test
  void shouldDeleteOldMessageAndSendNewOneIfThereIsAnyCommunicate() {
    givenUpdateWithTextAndData(ABILITIES, "callback-data");
    when(session.getNextCommunicate()).thenReturn(Optional.of(new Communicate(WEAPONS, List.of())));

    callbackService.execute(update);

    verify(telegramClient).deleteMessage(any(), any());
    verify(telegramClient).sendReplyKeyboard(any(), any(), any());
  }

  @Test
  void shouldHandleSpellCallbackRemoveMessageAndAskForTarget() {
    givenUpdateWithTextAndData(SPELL, "SACRED_FLAME");
    Game game = mock(Game.class);
    when(sessionFactory.getGame(any())).thenReturn(game);
    when(game.getPossibleTargets(anyString(), eq(Spell.SACRED_FLAME))).thenReturn(List.of("target1", "target2"));
    when(fightFactory.targetCommunicate(any())).thenReturn(Optional.of(new Communicate(TARGETS, List.of())));

    callbackService.execute(update);

    verify(telegramClient).deleteMessage(any(), any());
    verify(telegramClient).sendReplyKeyboard(any(), any(), any());
  }

  @Test
  void shouldHandleWeaponCallbackRemoveMessageAndAskForTarget() {
    givenUpdateWithTextAndData(WEAPON, "SHORTSWORD");
    when(fightFactory.targetCommunicate(any(), any(), any())).thenReturn(Optional.of(new Communicate(TARGETS, List.of())));

    callbackService.execute(update);

    verify(telegramClient).deleteMessage(any(), any());
    verify(telegramClient).sendReplyKeyboard(any(), any(), any());
  }

  @Test
  void shouldExecuteTurnWithSpellCastOnGivenTarget() {
    givenUpdateWithTextAndData(TARGETS, "target1");
    when(session.isSpellCasting()).thenReturn(true);
    when(session.getGenericData()).thenReturn(Map.of(SPELL, "SACRED_FLAME"));
    when(session.areAllTargetsAcquired()).thenReturn(true);

    callbackService.execute(update);

    ArgumentCaptor<SubjectTurn> captor = ArgumentCaptor.forClass(SubjectTurn.class);
    verify(commons).executeTurnAndDeleteMessage(any(), any(), captor.capture(), any(), any());
    SubjectTurn turn = captor.getValue();
    assertThat(turn.getActions()).isNotEmpty();
    assertThat(turn.getActions().get(0).getOperationType()).isEqualTo(OperationType.SPELL_CAST);
  }

  @Test
  void shouldExecuteTurnWithAttackOnGivenTarget() {
    givenUpdateWithTextAndData(TARGETS, "target1");
    when(session.isSpellCasting()).thenReturn(false);

    callbackService.execute(update);

    ArgumentCaptor<SubjectTurn> captor = ArgumentCaptor.forClass(SubjectTurn.class);
    verify(commons).executeTurnAndDeleteMessage(any(), any(), captor.capture(), any(), any());
    SubjectTurn turn = captor.getValue();
    assertThat(turn.getActions()).isNotEmpty();
    assertThat(turn.getActions().get(0).getOperationType()).isEqualTo(OperationType.ATTACK);
  }

  @Test
  void shouldExecuteTurnWithMove() {
    givenUpdateWithTextAndData(MOVE, "3");
    when(session.isMoving()).thenReturn(true);

    callbackService.execute(update);

    ArgumentCaptor<SubjectTurn> captor = ArgumentCaptor.forClass(SubjectTurn.class);
    verify(commons).executeTurnAndDeleteMessage(any(), any(), captor.capture(), any(), any());
    SubjectTurn turn = captor.getValue();
    assertThat(turn.getActions()).isNotEmpty();
    assertThat(turn.getActions().get(0).getOperationType()).isEqualTo(OperationType.MOVE);
  }
}