package dbryla.game.yetanotherengine.domain.operations;

import dbryla.game.yetanotherengine.domain.events.Event;

import java.util.HashSet;
import java.util.Set;

import dbryla.game.yetanotherengine.domain.subject.Subject;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OperationResult {

  private final Set<Subject> changedSubjects;
  private final Set<Event> emittedEvents;

  public OperationResult() {
    this.changedSubjects = new HashSet<>();
    this.emittedEvents = new HashSet<>();
  }

  public OperationResult(Subject changedTarget, Event event) {
    this.changedSubjects = new HashSet<>();
    this.changedSubjects.add(changedTarget);
    this.emittedEvents = new HashSet<>();
    this.emittedEvents.add(event);
  }

  public OperationResult addAll(Set<Subject> changedSubjects, Set<Event> emittedEvents) {
    this.changedSubjects.addAll(changedSubjects);
    this.emittedEvents.addAll(emittedEvents);
    return this;
  }

  public OperationResult add(Event event) {
    emittedEvents.add(event);
    return this;
  }

}
