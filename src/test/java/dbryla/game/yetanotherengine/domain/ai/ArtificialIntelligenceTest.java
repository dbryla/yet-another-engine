package dbryla.game.yetanotherengine.domain.ai;

import dbryla.game.yetanotherengine.domain.game.Game;
import dbryla.game.yetanotherengine.domain.game.SubjectTurn;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import dbryla.game.yetanotherengine.domain.equipment.Weapon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtificialIntelligenceTest {

  private static final String SUBJECT_NAME = "subject";
  private static final String TARGET_NAME = "acquiredTarget";
  private static final Weapon WEAPON = Weapon.SHORTSWORD;

  @Mock
  private Random random;

  @Mock
  private Game game;

  @Mock
  private Subject subject;

  @Mock
  private Subject target;

  @Mock
  private PositionService positionService;

  @InjectMocks
  private ArtificialIntelligence artificialIntelligence;

  @BeforeEach
  void setUp() {
    lenient().when(game.getSubject(eq(SUBJECT_NAME))).thenReturn(subject);
    lenient().when(game.getSubject(eq(TARGET_NAME))).thenReturn(target);
    when(game.getPossibleTargets(eq(subject), eq(WEAPON))).thenReturn(List.of(TARGET_NAME));

    when(subject.getName()).thenReturn(SUBJECT_NAME);
    when(subject.getEquippedWeapon()).thenReturn(WEAPON);
    when(random.nextInt(anyInt())).thenReturn(0);
  }

  @Test
  void shouldReturnActionWithAcquiredTarget() {
    artificialIntelligence.initSubject(game, subject);

    SubjectTurn subjectTurn = artificialIntelligence.action(SUBJECT_NAME);

    assertThat(subjectTurn.getActions()).isNotEmpty();
    assertThat(subjectTurn.getActions().get(0).getTargetNames()).contains(TARGET_NAME);
  }

  @Test
  void shouldReturnNextActionWithSameAcquiredTarget() {
    artificialIntelligence.initSubject(game, subject);
    artificialIntelligence.action(SUBJECT_NAME);

    SubjectTurn subjectTurn = artificialIntelligence.action(SUBJECT_NAME);

    assertThat(subjectTurn.getActions()).isNotEmpty();
    assertThat(subjectTurn.getActions().get(0).getTargetNames()).contains(TARGET_NAME);
  }

}