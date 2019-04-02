package dbryla.game.yetanotherengine;

public interface Subject {

  int getInitiativeModifier();

  String getName();

  boolean isTerminated();

  int getHealthPoints();

  int getArmorClass();

  Subject of(int healthPoints);

  String getAffiliation();

  SubjectIdentifier toIdentifier();
}
