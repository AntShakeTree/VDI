package com.opzoon.vdi.core.matcher;

import com.opzoon.vdi.core.domain.state.DesktopState;
import com.opzoon.vdi.core.fsm.State;
import com.opzoon.vdi.core.fsm.StateMatcher;

public class ConnectedDesktopMatcher implements StateMatcher
{

  @Override
  public boolean matches(State state)
  {
    DesktopState desktopState = (DesktopState) state;
    return desktopState.getConnectivity() == DesktopState.DESKTOP_CONNECTIVITY_CONNECTED;
  }

}
