package com.opzoon.vdi.core.fsm;

public interface Stateful
{

  State loadState();

  void mergeState(State state);

}
