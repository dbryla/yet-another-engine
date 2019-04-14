package dbryla.game.yetanotherengine.domain.subject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Transient;

import java.util.List;

@ToString(exclude = {"strengthModifier", "dexterityModifier", "constitutionModifier",
    "intelligenceModifier", "wisdomModifier", "charismaModifier"})
@EqualsAndHashCode
public class Abilities {

  private final int strength;
  private final int dexterity;
  private final int constitution;
  private final int intelligence;
  private final int wisdom;
  private final int charisma;

  @Getter
  @Transient
  private final int strengthModifier;
  @Getter
  @Transient
  private final int dexterityModifier;
  @Getter
  @Transient
  private final int constitutionModifier;
  @Getter
  @Transient
  private final int intelligenceModifier;
  @Getter
  @Transient
  private final int wisdomModifier;
  @Getter
  @Transient
  private final int charismaModifier;

  public Abilities(int strength, int dexterity, int constitution, int intelligence, int wisdom, int charisma) {
    this.strength = strength;
    this.dexterity = dexterity;
    this.constitution = constitution;
    this.intelligence = intelligence;
    this.wisdom = wisdom;
    this.charisma = charisma;
    this.strengthModifier = modifier(this.strength);
    this.dexterityModifier = modifier(this.dexterity);
    this.constitutionModifier = modifier(this.constitution);
    this.intelligenceModifier = modifier(this.intelligence);
    this.wisdomModifier = modifier(this.wisdom);
    this.charismaModifier = modifier(this.charisma);
  }

  private static int modifier(int ability) {
    return ability / 2 - 5;
  }

  public Abilities of(List<Integer> abilitiesModifiers) {
    return new Abilities(
        strength + abilitiesModifiers.get(0),
        dexterity + abilitiesModifiers.get(1),
        constitution + abilitiesModifiers.get(2),
        intelligence + abilitiesModifiers.get(3),
        wisdom + abilitiesModifiers.get(4),
        charisma + abilitiesModifiers.get(5));
  }

}
