package dbryla.game.yetanotherengine.domain.subject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Transient;

import java.util.LinkedList;
import java.util.List;

@ToString(exclude = {"strengthModifier", "dexterityModifier", "constitutionModifier",
    "intelligenceModifier", "wisdomModifier", "charismaModifier"})
@EqualsAndHashCode
@NoArgsConstructor
public class Abilities {

  private int strength;
  private int dexterity;
  private int constitution;
  private int intelligence;
  private int wisdom;
  private int charisma;

  @Getter
  @Transient
  @JsonIgnore
  private int strengthModifier;
  @Getter
  @Transient
  @JsonIgnore
  private int dexterityModifier;
  @Getter
  @Transient
  @JsonIgnore
  private int constitutionModifier;
  @Getter
  @Transient
  @JsonIgnore
  private int intelligenceModifier;
  @Getter
  @Transient
  @JsonIgnore
  private int wisdomModifier;
  @Getter
  @Transient
  @JsonIgnore
  private int charismaModifier;

  public Abilities(int strength, int dexterity, int constitution, int intelligence, int wisdom, int charisma) {
    this.setStrength(strength);
    this.setDexterity(dexterity);
    this.setConstitution(constitution);
    this.setIntelligence(intelligence);
    this.setWisdom(wisdom);
    this.setCharisma(charisma);
  }

  public void setStrength(int strength) {
    this.strength = strength;
    this.strengthModifier = modifier(this.strength);
  }

  public void setDexterity(int dexterity) {
    this.dexterity = dexterity;
    this.dexterityModifier = modifier(this.dexterity);
  }

  public void setConstitution(int constitution) {
    this.constitution = constitution;
    this.constitutionModifier = modifier(this.constitution);
  }

  public void setIntelligence(int intelligence) {
    this.intelligence = intelligence;
    this.intelligenceModifier = modifier(this.intelligence);
  }

  public void setWisdom(int wisdom) {
    this.wisdom = wisdom;
    this.wisdomModifier = modifier(this.wisdom);
  }

  public void setCharisma(int charisma) {
    this.charisma = charisma;
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

  public Abilities of(int firstIndexOfAbilityToModify, int secondIndexOfAbilityToModify) {
    List<Integer> abilitiesModifiers = new LinkedList<>();
    for (int i = 0; i < 6; i++) {
      if (i == firstIndexOfAbilityToModify || i == secondIndexOfAbilityToModify) {
        abilitiesModifiers.add(1);
      } else {
        abilitiesModifiers.add(0);
      }
    }
    return of(abilitiesModifiers);
  }
}
