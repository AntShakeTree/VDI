package com.opzoon.vdi.core.event;

import java.util.Set;

import com.opzoon.vdi.core.fsm.State;
import com.opzoon.vdi.core.operations.Event;

public class StateUpdatedEvent implements Event
{
  
  private final Set<State> effectedStates;

  public StateUpdatedEvent(Set<State> effectedStates)
  {
    this.effectedStates = effectedStates;
  }

  public Set<State> getEffectedStates()
  {
    return effectedStates;
  }

}
