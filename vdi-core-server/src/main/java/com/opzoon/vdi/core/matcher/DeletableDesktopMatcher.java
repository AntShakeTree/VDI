package com.opzoon.vdi.core.matcher;

import com.opzoon.vdi.core.domain.state.DesktopState;
import com.opzoon.vdi.core.fsm.State;
import com.opzoon.vdi.core.fsm.StateMatcher;

public class DeletableDesktopMatcher implements StateMatcher
{

  @Override
  public boolean matches(State state)
  {
    DesktopState desktopState = (DesktopState) state;
    return desktopState.getPhase() == DesktopState.DESKTOP_PHASE_CREATING
        || desktopState.getPhase() == DesktopState.DESKTOP_PHASE_NORMAL
        || desktopState.getPhase() == DesktopState.DESKTOP_PHASE_DEFICIENT;
  }

}
