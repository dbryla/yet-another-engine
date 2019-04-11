package dbryla.game.yetanotherengine.domain;

import lombok.Getter;
import lombok.ToString;

@ToString(exclude = {"strengthModifier", "dexterityModifier", "constitutionModifier", "intelligenceModifier", "wisdomModifier", "charismaModifier"})
public class Abilities {

  private final int strength;
  private final int dexterity;
  private final int constitution;
  private final int intelligence;
  private final int wisdom;
  private final int charisma;

  @Getter
  private final int strengthModifier;
  @Getter
  private final int dexterityModifier;
  @Getter
  private final int constitutionModifier;
  @Getter
  private final int intelligenceModifier;
  @Getter
  private final int wisdomModifier;
  @Getter
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

}
