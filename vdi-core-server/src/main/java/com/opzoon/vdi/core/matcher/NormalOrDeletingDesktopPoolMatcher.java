package com.opzoon.vdi.core.matcher;

import com.opzoon.vdi.core.domain.state.DesktopPoolState;
import com.opzoon.vdi.core.fsm.State;
import com.opzoon.vdi.core.fsm.StateMatcher;

public class NormalOrDeletingDesktopPoolMatcher implements StateMatcher
{

  @Override
  public boolean matches(State state)
  {
    DesktopPoolState desktopPoolState = (DesktopPoolState) state;
    return desktopPoolState.getPhase() == DesktopPoolState.DESKTOP_POOL_PHASE_NORMAL
        || desktopPoolState.getPhase() == DesktopPoolState.DESKTOP_POOL_PHASE_DELETING;
  }

}
