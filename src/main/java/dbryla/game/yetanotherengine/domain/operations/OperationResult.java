package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.events.Event;
import dbryla.game.yetanotherengine.domain.subject.State;
import dbryla.game.yetanotherengine.domain.subject.Subject;
import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OperationResult {

  private final List<State> changedSubjects;
  private final List<Event> emittedEvents;

  public OperationResult() {
    this.changedSubjects = new LinkedList<>();
    this.emittedEvents = new LinkedList<>();
  }

  public OperationResult(State changedSubject, Event event) {
    this.changedSubjects = new LinkedList<>();
    this.changedSubjects.add(changedSubject);
    this.emittedEvents = new LinkedList<>();
    this.emittedEvents.add(event);
  }

  public void addAll(List<State> changedSubjects, List<Event> emittedEvents) {
    this.changedSubjects.addAll(changedSubjects);
    this.emittedEvents.addAll(emittedEvents);
  }

  public OperationResult add(Event event) {
    emittedEvents.add(event);
    return this;
  }

  public void add(State changedSubject, Event event) {
    this.changedSubjects.add(changedSubject);
    this.emittedEvents.add(event);
  }

  public void copyFrom(OperationResult operationResult) {
    addAll(operationResult.getChangedSubjects(), operationResult.getEmittedEvents());
  }
}
