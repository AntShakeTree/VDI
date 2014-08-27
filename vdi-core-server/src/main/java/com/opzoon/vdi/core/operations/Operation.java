package com.opzoon.vdi.core.operations;

import java.util.List;
import java.util.Map;

import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.fsm.Entity;
import com.opzoon.vdi.core.fsm.StateMatcher;

public interface Operation
{
  
  Object execute(OperationContext operationContext) throws CommonException;

  boolean rejects(Operation operation);

  boolean delays(Operation operation);

  boolean onEvent(OperationContext operationContext, Event event) throws CommonException;

  Map<Entity, StateMatcher> getNeededStates();
  
  List<Object> getParams();

}
